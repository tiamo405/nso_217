"""Chạy script python (hoặc stdin khi tham số là "-") với Path.write_text ghi LF, giống hành vi trên Linux."""
import pathlib
import runpy
import sys


def _write_text(self, data, encoding=None, errors=None, newline=None):
    with open(self, "w", encoding=encoding, errors=errors, newline="\n" if newline is None else newline) as fh:
        return fh.write(data)


pathlib.Path.write_text = _write_text

script = sys.argv[1]
sys.argv = sys.argv[1:]
if script == "-":
    code = sys.stdin.read()
    exec(compile(code, "<stdin>", "exec"), {"__name__": "__main__", "__builtins__": __builtins__})
else:
    sys.path.insert(0, str(pathlib.Path(script).resolve().parent))
    runpy.run_path(script, run_name="__main__")
