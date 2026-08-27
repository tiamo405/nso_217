from __future__ import annotations

import os
from dataclasses import dataclass
from pathlib import Path


@dataclass(frozen=True)
class Settings:
    repo_dir: Path
    headless_dir: Path
    scripts_dir: Path
    workers_dir: Path
    account_csv: Path
    runtime_dir: Path
    max_upload_bytes: int = 2 * 1024 * 1024
    command_timeout: int = 30
    build_timeout: int = 15 * 60

    @classmethod
    def from_env(cls) -> "Settings":
        repo_dir = Path(
            os.environ.get("NSO_REPO_DIR", Path(__file__).resolve().parents[1])
        ).resolve()
        headless_dir = Path(
            os.environ.get("NSO_HEADLESS_DIR", repo_dir / "headless-runtime")
        ).resolve()
        return cls(
            repo_dir=repo_dir,
            headless_dir=headless_dir,
            scripts_dir=headless_dir / "scripts",
            workers_dir=Path(
                os.environ.get("HEADLESS_WORKERS_DIR", headless_dir / "workers")
            ).resolve(),
            account_csv=Path(
                os.environ.get("ACCOUNT_CSV", repo_dir / "account.csv")
            ).resolve(),
            runtime_dir=Path(
                os.environ.get("NSO_WEB_RUNTIME_DIR", headless_dir / "run" / "web-control")
            ).resolve(),
        )

    def command_env(self) -> dict[str, str]:
        env = os.environ.copy()
        env["HEADLESS_WORKERS_DIR"] = str(self.workers_dir)
        env["ACCOUNT_CSV"] = str(self.account_csv)
        return env
