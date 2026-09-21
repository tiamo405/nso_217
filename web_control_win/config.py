from __future__ import annotations

import os
import sys
from dataclasses import dataclass
from pathlib import Path
from typing import Dict

from server_config import DEFAULT_SERVER, normalize_server


@dataclass(frozen=True)
class Settings:
    repo_dir: Path
    runtime_dir: Path
    workers_dir: Path
    win_manager_py: Path
    account_csv: Path
    web_runtime_dir: Path
    # Optional to keep compatibility with tests/embedders that construct
    # Settings manually.  from_env() always fills this path.
    ta_thu_dir: Path | None = None
    max_upload_bytes: int = 2 * 1024 * 1024
    command_timeout: int = 30
    build_timeout: int = 15 * 60

    @classmethod
    def from_env(cls) -> "Settings":
        repo_dir = Path(
            os.environ.get("NSO_REPO_DIR", Path(__file__).resolve().parents[1])
        ).resolve()

        runtime_dir = Path(
            os.environ.get("NSO_OPTIMIZED_DIR", repo_dir / "optimized-runtime")
        ).resolve()

        workers_dir = Path(
            os.environ.get("OPTIMIZED_WORKERS_DIR", runtime_dir / "workers")
        ).resolve()

        win_manager_py = Path(
            os.environ.get("NSO_WIN_MANAGER", runtime_dir / "windows" / "win_manager.py")
        ).resolve()

        account_csv = Path(
            os.environ.get("ACCOUNT_CSV", repo_dir / "account.csv")
        ).resolve()

        web_runtime_dir = Path(
            os.environ.get(
                "NSO_WEB_WIN_RUNTIME_DIR",
                runtime_dir / "run" / "web-control-win",
            )
        ).resolve()

        ta_thu_dir = Path(
            os.environ.get("NSO_TA_THU_DIR", repo_dir / "ta-thu-runtime")
        ).resolve()

        return cls(
            repo_dir=repo_dir,
            runtime_dir=runtime_dir,
            workers_dir=workers_dir,
            win_manager_py=win_manager_py,
            account_csv=account_csv,
            web_runtime_dir=web_runtime_dir,
            ta_thu_dir=ta_thu_dir,
        )

    @property
    def ta_thu_root(self) -> Path:
        return (self.ta_thu_dir or (self.repo_dir / "ta-thu-runtime")).resolve()

    @property
    def ta_thu_manager_py(self) -> Path:
        return self.ta_thu_root / "windows" / "ta_thu_manager.py"

    @property
    def ta_thu_workers_dir(self) -> Path:
        return self.ta_thu_root / "workers"

    def command_env(self, server: str | None = None) -> Dict[str, str]:
        env = os.environ.copy()
        env["OPTIMIZED_WORKERS_DIR"] = str(self.workers_dir)
        env["HEADLESS_WORKERS_DIR"] = str(self.workers_dir)
        env["ACCOUNT_CSV"] = str(self.account_csv)
        env["TA_THU_ACCOUNT_CSV"] = str(self.account_csv)
        env["TA_THU_WORKERS_DIR"] = str(self.ta_thu_workers_dir)
        env["TA_THU_CLASSES_DIR"] = str(self.ta_thu_root / "build" / "classes")
        env["TA_THU_STATE_DIR"] = str(self.ta_thu_root / "ta-thu-state")
        env["PYTHONIOENCODING"] = "utf-8"
        env["PYTHONUTF8"] = "1"
        try:
            env["NSO_SERVER"] = normalize_server(server or env.get("NSO_SERVER"))
        except ValueError:
            env["NSO_SERVER"] = DEFAULT_SERVER
        return env
