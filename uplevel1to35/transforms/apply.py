#!/usr/bin/env python3
"""Vá nguồn đã ghép cho runtime uplevel1to35 (chạy SAU uplevel/scripts/apply-transforms.sh và
SAU khi copy uplevel35/src + uplevel1to35/src vào thư mục staging).

    python3 uplevel1to35/transforms/apply.py <work_src_dir>

Mỗi bước tìm đúng 1 needle; không khớp -> thoát lỗi (src/ hoặc uplevel/ đã đổi, cần cập nhật needle).

- AccountAutoManager (bản uplevel/overrides): header "account,password"; onGameReady chọn As20/As35
  theo Uplevel1to35.phase(); onAs20Finished chạy lại CÙNG nhân vật trong JVM mới để vào As35
  (biến static của As20 không dính sang As35); thêm onUplevel35Finished (As35 gọi).
- Controller: log/chuyển lời NPC cho As35 (thay patch 002 của uplevel35) và chống NPE khi thiếu RMS
  skill (nửa sau patch 003; nửa đầu "lưu cả hai khóa" uplevel đã làm).
- As35 / AccountAutoManager: UplevelStatus ghi runtime=uplevel1to35 cho web monitor.
"""
from __future__ import annotations

import pathlib
import sys


def replace_once(path: pathlib.Path, source: str, needle: str, replacement: str, what: str) -> str:
    count = source.count(needle)
    if count != 1:
        raise SystemExit(f"uplevel1to35 transform lỗi: {path.name}: cần đúng 1 chỗ '{what}', thấy {count}")
    return source.replace(needle, replacement, 1)


def patch_account_manager(path: pathlib.Path) -> None:
    src = path.read_text(encoding="utf-8")

    src = replace_once(
        path, src,
        '        if (username.equalsIgnoreCase("username") && password.equalsIgnoreCase("password")) {\n',
        '        if ((username.equalsIgnoreCase("username") || username.equalsIgnoreCase("account"))\n'
        '                && password.equalsIgnoreCase("password")) {\n',
        "header account.csv")

    needle = ('        BotMetrics.event("game_ready", me.nClass == null ? "" : String.valueOf(me.nClass.classId), me.clevel);\n'
              '        int mode = Integer.getInteger("nso.as20.mode", 0);\n')
    dispatch = '''        BotMetrics.event("game_ready", me.nClass == null ? "" : String.valueOf(me.nClass.classId), me.clevel);
        int uplevelPhase = Uplevel1to35.phase(me);
        Uplevel1to35.log("GAME READY " + Uplevel1to35.describe(me));
        if (uplevelPhase == Uplevel1to35.PHASE_DONE) {
            skipCurrentCharacter("UPLEVEL1TO35: nhân vật " + me.cName + " level=" + me.clevel
                    + " ctaskId=" + me.ctaskId + " đã xong lộ trình 1 -> 35, bỏ qua");
            return;
        }
        if (uplevelPhase == Uplevel1to35.PHASE_AS35) {
            startUplevel35(me);
            return;
        }
        int mode = Integer.getInteger("nso.as20.mode", 0);
'''
    src = replace_once(path, src, needle, dispatch, "onGameReady game_ready + mode")

    needle = '''        BotMetrics.finish("completed", "uplevel finished task 17");
        skipCurrentCharacter("uplevel hoàn thành nhiệm vụ Jaian, chuyển nhân vật/tài khoản tiếp theo");
    }
'''
    replacement = '''        if (Uplevel1to35.phaseAfterAs20(me) == Uplevel1to35.PHASE_AS35) {
            Uplevel1to35.log("PHASE as20 -> as35 " + Uplevel1to35.describe(me));
            relaunchForUplevel35(me);
            return;
        }
        BotMetrics.finish("completed", "uplevel finished task 17");
        skipCurrentCharacter("uplevel hoàn thành nhiệm vụ Jaian, chuyển nhân vật/tài khoản tiếp theo");
    }

    private static void startUplevel35(Char me) {
        System.out.println("UPLEVEL35: bắt đầu nhân vật " + me.cName + " level=" + me.clevel);
        Code.fieldAA((Auto) new As35());
    }

    /**
     * Xong giai đoạn As20 (đã trả nhiệm vụ 17): chạy lại CHÍNH nhân vật này trong JVM mới để As35 bắt đầu
     * với static sạch. Ghi tiến độ với accountIndex/characterIndex hiện tại rồi thoát mã 3; worker-loop.sh
     * chạy JVM mới, đăng nhập lại, onGameReady thấy ctaskId >= 18 -> As35.
     * Không chạy chế độ JVM riêng (hoặc ghi tiến độ lỗi): chuyển sang As35 ngay trong JVM hiện tại.
     */
    private static void relaunchForUplevel35(final Char me) {
        reconnecting = false;
        waitingForGame = false;
        switching = true;
        Code.fieldAG();
        (new Thread(new Runnable() {
            public void run() {
                sleep(1500L);
                if (PROCESS_PER_CHARACTER && saveProgress()) {
                    Uplevel1to35.log("RELAUNCH JVM mới cho " + me.cName + " (tài khoản "
                            + getCurrentUsername() + ", nhân vật #" + (characterIndex + 1) + ") để chạy As35");
                    enabled = false;
                    try {
                        Session_ME.gI().gameAC();
                    } catch (Exception ignored) {
                    }
                    sleep(500L);
                    System.exit(HANDOFF_EXIT_CODE);
                    return;
                }
                synchronized (AccountAutoManager.class) {
                    Uplevel1to35.log("chuyển As35 trong JVM hiện tại cho " + me.cName);
                    switching = false;
                    startUplevel35(Char.getMyChar());
                }
            }
        })).start();
    }

    /** As35 báo xong nhân vật (đủ level hoặc gặp nhiệm vụ chưa hỗ trợ). */
    public static synchronized void onUplevel35Finished(String reason) {
        if (!enabled || switching) {
            return;
        }
        Char me = Char.getMyChar();
        BotMetrics.event("uplevel35_done", me == null ? "" : String.valueOf(me.ctaskId),
                me == null ? 0 : me.clevel);
        skipCurrentCharacter(reason);
    }
'''
    src = replace_once(path, src, needle, replacement, "onAs20Finished skip")

    src = replace_once(path, src, 'UplevelStatus.start("UPLEVEL", "uplevel");',
                       'UplevelStatus.start("UPLEVEL", "uplevel1to35");', "UplevelStatus uplevel")
    path.write_text(src, encoding="utf-8")


def patch_controller(path: pathlib.Path) -> None:
    src = path.read_text(encoding="utf-8")
    needle = "                            ChatPopup.gameAA(utf13 = fieldAB.reader().readUTF(), var78);\n"
    src = replace_once(path, src, needle,
                       needle + "                            Uplevel35.onNpcMessage(var78.template.npcTemplateId, utf13);\n",
                       "NPC popup handler")

    needle = '''                    if (Char.getMyChar().gameBA()) {
                        gameAB(new DataInputStream(new ByteArrayInputStream(RMS.gameAA("skill"))));
                    } else {
                        gameAB(new DataInputStream(new ByteArrayInputStream(RMS.gameAA("skillnhanban"))));
                    }
'''
    replacement = '''                    String skillRmsKey = Char.getMyChar().gameBA() ? "skill" : "skillnhanban";
                    byte[] skillRmsData = RMS.gameAA(skillRmsKey);
                    if (skillRmsData != null && skillRmsData.length > 0) {
                        gameAB(new DataInputStream(new ByteArrayInputStream(skillRmsData)));
                    } else {
                        System.out.println("UPLEVEL1TO35 WARN: thiếu RMS " + skillRmsKey + ", yêu cầu tải lại skill");
                        GameScr.vcSkill = -1;
                        Service.gI().updateSkill();
                    }
'''
    src = replace_once(path, src, needle, replacement, "load RMS skill")
    path.write_text(src, encoding="utf-8")


def patch_as35(path: pathlib.Path) -> None:
    src = path.read_text(encoding="utf-8")
    src = replace_once(path, src, 'UplevelStatus.start("UPLEVEL35", "uplevel35");',
                       'UplevelStatus.start("UPLEVEL35", "uplevel1to35");', "UplevelStatus uplevel35")
    path.write_text(src, encoding="utf-8")


def main(argv: list[str]) -> int:
    if len(argv) != 2:
        print(__doc__, file=sys.stderr)
        return 2
    work = pathlib.Path(argv[1])
    patch_account_manager(work / "AccountAutoManager.java")
    patch_controller(work / "Controller.java")
    patch_as35(work / "As35.java")
    print("uplevel1to35 transforms: AccountAutoManager, Controller, As35")
    return 0


if __name__ == "__main__":
    sys.exit(main(sys.argv))
