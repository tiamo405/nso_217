"""Cross-platform builder for the Tà Thú headless runtime.

The Linux build is intentionally still driven by ``build-ta-thu.sh``.  This
module mirrors that build on Windows so the web controller does not depend on
Git Bash, WSL, ``sed`` or ``patch`` being installed.
"""

from __future__ import annotations

import os
import re
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path


TA_THU_DIR = Path(__file__).resolve().parents[1]
REPO_DIR = TA_THU_DIR.parent
BUILD_ROOT = Path(
    os.environ.get("TA_THU_BUILD_DIR", TA_THU_DIR / "build")
).resolve()


def _copy_tree(source: Path, destination: Path) -> None:
    if not source.is_dir():
        raise RuntimeError(f"Không tìm thấy thư mục build: {source}")
    shutil.copytree(source, destination, dirs_exist_ok=True)


def _replace_all(path: Path, old: str, new: str) -> int:
    text = path.read_text(encoding="utf-8").replace("\r\n", "\n")
    count = text.count(old)
    if count:
        path.write_text(text.replace(old, new), encoding="utf-8", newline="\n")
    return count


def _insert_after(path: Path, needle: str, inserted: str) -> int:
    text = path.read_text(encoding="utf-8").replace("\r\n", "\n")
    count = text.count(needle)
    if count:
        text = text.replace(needle, needle + "\n" + inserted)
        path.write_text(text, encoding="utf-8", newline="\n")
    return count


def _apply_unified_patch(path: Path, patch_file: Path) -> None:
    """Apply the small repository-local unified patches without GNU patch."""

    source = path.read_text(encoding="utf-8").replace("\r\n", "\n")
    lines = source.splitlines()
    patch_lines = patch_file.read_text(encoding="utf-8").replace("\r\n", "\n").splitlines()
    index = 0
    target_name = path.name

    while index < len(patch_lines) and not patch_lines[index].startswith("@@"):
        index += 1

    offset = 0
    while index < len(patch_lines):
        header = patch_lines[index]
        match = re.match(r"@@ -(\d+)(?:,(\d+))? \+(\d+)(?:,(\d+))? @@", header)
        if not match:
            raise RuntimeError(f"Patch không hợp lệ {patch_file}: {header}")
        old_start = int(match.group(1)) - 1 + offset
        index += 1
        old_lines: list[str] = []
        new_lines: list[str] = []
        while index < len(patch_lines) and not patch_lines[index].startswith("@@"):
            line = patch_lines[index]
            index += 1
            if line == r"\ No newline at end of file":
                continue
            # A few repository-local patches were generated with an empty
            # context line instead of a single leading space.  Treat both
            # spellings as the same blank source line.
            if line == "":
                old_lines.append("")
                new_lines.append("")
                continue
            if line[0] not in " +-":
                raise RuntimeError(f"Patch không hợp lệ {patch_file}: {line}")
            if line[0] in " -":
                old_lines.append(line[1:])
            if line[0] in " +":
                new_lines.append(line[1:])

        position = old_start
        if lines[position:position + len(old_lines)] != old_lines:
            position = -1
            for candidate in range(0, len(lines) - len(old_lines) + 1):
                if lines[candidate:candidate + len(old_lines)] == old_lines:
                    position = candidate
                    break
        if position < 0:
            raise RuntimeError(
                f"Không tìm thấy context của {target_name} khi áp dụng {patch_file.name}"
            )
        lines[position:position + len(old_lines)] = new_lines
        offset += len(new_lines) - len(old_lines)

    path.write_text("\n".join(lines) + "\n", encoding="utf-8", newline="\n")


def _apply_ta_thu_hooks(work_src: Path) -> None:
    replacements = [
        ("GameMidlet.java", "AccountAutoManager.start()", "TaThuAccountManager.start()"),
        ("Controller.java", "AccountAutoManager.onDisconnected()", "TaThuAccountManager.onDisconnected()"),
        ("Session_ME.java", "AccountAutoManager.onReconnectRequested()", "TaThuAccountManager.onReconnectRequested()"),
        ("Code.java", "AccountAutoManager.isRunning()", "TaThuAccountManager.isRunning()"),
        ("Controller.java", "AccountAutoManager.onServerMessage(utf)", "TaThuAccountManager.onServerMessage(utf)"),
        ("Controller.java", "AccountAutoManager.onCaveEntered()", "TaThuAccountManager.onCaveEntered()"),
        ("AutoEnterCave.java", "AccountAutoManager.onCaveEntered()", "TaThuAccountManager.onCaveEntered()"),
        ("Controller.java", "AccountAutoManager.onCharacterBelowLevel30(utf13)", "TaThuAccountManager.onCharacterBelowLevel30(utf13)"),
        ("Controller.java", "AccountAutoManager.onGameReady()", "TaThuAccountManager.onGameReady()"),
        ("Controller.java", "AccountAutoManager.onCharacterList(var49.name)", "TaThuAccountManager.onCharacterList(var49.name)"),
        ("AutoPrepareNvhn.java", "Code.fieldAD();", "TaThuAccountManager.onPreparationFinished();"),
        ("AutoFlipNvhn.java", "AccountAutoManager.onPostDailyFlipFinished()", "TaThuAccountManager.onPostDailyFlipFinished()"),
    ]
    for filename, old, new in replacements:
        path = work_src / filename
        if not path.is_file() or _replace_all(path, old, new) == 0:
            raise RuntimeError(f"Build lỗi: không chèn được hook '{new}' vào {filename}")

    controller = work_src / "Controller.java"
    if _insert_after(
        controller,
        "ChatPopup.gameAA(utf13 = fieldAB.reader().readUTF(), var78);",
        "                            TaThuAccountManager.onNpcMessage(var78.template.npcTemplateId, utf13);",
    ) == 0:
        raise RuntimeError("Build lỗi: không chèn được hook onNpcMessage")
    if _insert_after(
        controller,
        "var87.count = fieldAB.reader().readInt();",
        "                            TaThuAccountManager.onTaskOrderProgress(var87);",
    ) == 0:
        raise RuntimeError("Build lỗi: không chèn được hook onTaskOrderProgress")
    if _insert_after(
        controller,
        "LockGame.fieldAN();",
        "                    TaThuAccountManager.onTaskOrderRemoved(var85);",
    ) == 0:
        raise RuntimeError("Build lỗi: không chèn được hook onTaskOrderRemoved")

    info_me = work_src / "InfoMe.java"
    if _insert_after(
        info_me,
        "CodePhu.fieldAA(var0);",
        "            TaThuAccountManager.onServerMessage(var0);",
    ) == 0:
        raise RuntimeError("Build lỗi: không chèn được hook InfoMe")

    # Desktop JVM resource lookup fixes used by the shell build.
    for filename, old, new in (
        (
            "TileMap.java",
            '"".getClass().getResourceAsStream("/map/" + var1)',
            'TileMap.class.getResourceAsStream("/map/" + var1)',
        ),
        (
            "RMS.java",
            '"".getClass().getResourceAsStream(var0)',
            'RMS.class.getResourceAsStream(var0)',
        ),
        (
            "Res.java",
            '"".getClass().getResourceAsStream(var0)',
            'Res.class.getResourceAsStream(var0)',
        ),
    ):
        path = work_src / filename
        _replace_all(path, old, new)

    patches_dir = TA_THU_DIR / "patches"
    for patch_file in sorted(patches_dir.glob("*.patch")):
        _apply_unified_patch(work_src / "Controller.java", patch_file)

    required = (
        ("GameMidlet.java", "TaThuAccountManager.start()"),
        ("Controller.java", "TaThuAccountManager.onDisconnected()"),
        ("Controller.java", "TaThuAccountManager.onNpcMessage("),
        ("Controller.java", "TaThuAccountManager.onTaskOrderProgress("),
        ("Controller.java", "TaThuAccountManager.onTaskOrderRemoved("),
        ("Controller.java", "TaThuAccountManager.onGameReady()"),
        ("Controller.java", "TaThuAccountManager.onCharacterList("),
        ("InfoMe.java", "TaThuAccountManager.onServerMessage("),
        ("AutoPrepareNvhn.java", "TaThuAccountManager.onPreparationFinished()"),
        ("AutoFlipNvhn.java", "TaThuAccountManager.onPostDailyFlipFinished()"),
        ("AutoEnterCave.java", "TaThuAccountManager.onCaveEntered()"),
    )
    for filename, pattern in required:
        if pattern not in (work_src / filename).read_text(encoding="utf-8"):
            raise RuntimeError(f"Build lỗi: thiếu hook '{pattern}' trong {filename}")


def build() -> Path:
    account_csv = Path(os.environ.get("TA_THU_ACCOUNT_CSV", REPO_DIR / "account.csv"))
    if not account_csv.is_file():
        raise RuntimeError(f"Không tìm thấy account CSV: {account_csv}")

    javac = os.environ.get("JAVAC_BIN", "javac")
    if shutil.which(javac) is None and not Path(javac).is_file():
        raise RuntimeError("Không tìm thấy javac trong PATH")

    build_parent = BUILD_ROOT.parent
    build_parent.mkdir(parents=True, exist_ok=True)
    staging = Path(tempfile.mkdtemp(prefix=f".{BUILD_ROOT.name}.build.", dir=build_parent))
    classes = staging / "classes"
    work_src = staging / "src-repo"
    sources_file = staging / "sources.txt"
    backup = build_parent / f".{BUILD_ROOT.name}.old.{os.getpid()}"

    try:
        classes.mkdir(parents=True)
        _copy_tree(REPO_DIR / "src", work_src)
        _copy_tree(TA_THU_DIR / "src", work_src)
        _copy_tree(TA_THU_DIR / "overrides", work_src)
        _apply_ta_thu_hooks(work_src)

        java_sources = sorted(
            [path.as_posix() for path in (REPO_DIR / "headless-runtime" / "src").rglob("*.java")]
            + [path.as_posix() for path in work_src.rglob("*.java")]
        )
        sources_file.write_text("\n".join(java_sources) + "\n", encoding="utf-8", newline="\n")
        command = [
            javac,
            "-encoding", "UTF-8",
            "-source", "8",
            "-target", "8",
            "-Xlint:none",
            "-d", str(classes),
            f"@{sources_file}",
        ]
        result = subprocess.run(command, cwd=REPO_DIR)
        if result.returncode != 0:
            raise RuntimeError(f"javac thất bại với exit code {result.returncode}")

        _copy_tree(work_src, classes)
        for java_file in classes.rglob("*.java"):
            java_file.unlink()
        map_dir = classes / "map"
        map_dir.mkdir(parents=True, exist_ok=True)
        for map_id in range(160):
            (map_dir / str(map_id)).touch(exist_ok=True)
        shutil.copy2(account_csv, classes / "account.csv")
        extra = REPO_DIR / "delllllllllll.txt"
        if extra.is_file():
            shutil.copy2(extra, classes / extra.name)

        if not (classes / "HeadlessMain.class").is_file() or not (
            classes / "TaThuAccountManager.class"
        ).is_file():
            raise RuntimeError("Build lỗi: thiếu class runtime Tà Thú")

        if backup.exists():
            shutil.rmtree(backup)
        if BUILD_ROOT.exists():
            shutil.move(str(BUILD_ROOT), str(backup))
        try:
            shutil.move(str(staging), str(BUILD_ROOT))
        except Exception:
            if backup.exists() and not BUILD_ROOT.exists():
                shutil.move(str(backup), str(BUILD_ROOT))
            raise
        staging = Path()
        if backup.exists():
            shutil.rmtree(backup)
        print(f"Ta Thu runtime built at {BUILD_ROOT / 'classes'}", flush=True)
        return BUILD_ROOT
    finally:
        if staging != Path() and staging.exists():
            shutil.rmtree(staging, ignore_errors=True)
        if backup.exists():
            shutil.rmtree(backup, ignore_errors=True)


if __name__ == "__main__":
    try:
        build()
    except Exception as exc:
        print(f"Build Tà Thú lỗi: {exc}", file=sys.stderr)
        raise SystemExit(1)
