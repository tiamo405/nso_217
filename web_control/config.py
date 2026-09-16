from __future__ import annotations

import os
from dataclasses import dataclass
from pathlib import Path

from server_config import DEFAULT_SERVER, normalize_server


@dataclass(frozen=True)
class Settings:
    repo_dir: Path
    headless_dir: Path
    scripts_dir: Path
    workers_dir: Path
    account_csv: Path
    runtime_dir: Path
    ta_thu_dir: Path
    max_upload_bytes: int = 2 * 1024 * 1024
    command_timeout: int = 30
    build_timeout: int = 15 * 60

    @classmethod
    def from_env(cls) -> "Settings":
        repo_dir = Path(
            os.environ.get("NSO_REPO_DIR", Path(__file__).resolve().parents[1])
        ).resolve()
        # Ưu tiên NSO_OPTIMIZED_DIR hoặc NSO_HEADLESS_DIR, mặc định là optimized-runtime
        headless_dir = Path(
            os.environ.get(
                "NSO_OPTIMIZED_DIR",
                os.environ.get("NSO_HEADLESS_DIR", repo_dir / "optimized-runtime"),
            )
        ).resolve()
        workers_dir = Path(
            os.environ.get(
                "OPTIMIZED_WORKERS_DIR",
                os.environ.get("HEADLESS_WORKERS_DIR", headless_dir / "workers"),
            )
        ).resolve()
        ta_thu_dir = Path(
            os.environ.get("NSO_TA_THU_DIR", repo_dir / "ta-thu-runtime")
        ).resolve()
        return cls(
            repo_dir=repo_dir,
            headless_dir=headless_dir,
            scripts_dir=headless_dir / "scripts",
            workers_dir=workers_dir,
            account_csv=Path(
                os.environ.get("ACCOUNT_CSV", repo_dir / "account.csv")
            ).resolve(),
            runtime_dir=Path(
                os.environ.get(
                    "NSO_WEB_RUNTIME_DIR", headless_dir / "run" / "web-control"
                )
            ).resolve(),
            ta_thu_dir=ta_thu_dir,
        )

    def command_env(self, server: str | None = None) -> dict[str, str]:
        env = os.environ.copy()
        env["OPTIMIZED_WORKERS_DIR"] = str(self.workers_dir)
        env["HEADLESS_WORKERS_DIR"] = str(self.workers_dir)
        env["ACCOUNT_CSV"] = str(self.account_csv)
        env["TA_THU_WORKERS_DIR"] = str(self.ta_thu_dir / "workers")
        try:
            env["NSO_SERVER"] = normalize_server(server or env.get("NSO_SERVER"))
        except ValueError:
            env["NSO_SERVER"] = DEFAULT_SERVER
        return env
