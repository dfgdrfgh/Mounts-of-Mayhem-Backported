"""Launch the Fabric development client under Xvfb and verify resource startup."""
import os
from pathlib import Path
import signal
import subprocess
import time

ROOT = Path(__file__).resolve().parents[2]
LOG = ROOT / "fabric/build/client-smoke.log"
LOG.parent.mkdir(parents=True, exist_ok=True)
environment = dict(os.environ, LIBGL_ALWAYS_SOFTWARE="1", ALSOFT_DRIVERS="null")
failure_markers = ("Mixin apply for mod mombackport failed", "Mixin transformation of",
                   "Minecraft has crashed", "Reported exception thrown", "BUILD FAILED")

with LOG.open("w") as output:
    process = subprocess.Popen(
        ["xvfb-run", "-a", "./gradlew", ":fabric:runClient", "--stacktrace"],
        cwd=ROOT, env=environment, stdout=output, stderr=subprocess.STDOUT, start_new_session=True)
    deadline = time.monotonic() + 240
    ready_at = None
    try:
        while time.monotonic() < deadline:
            text = LOG.read_text(errors="replace")
            if any(marker in text for marker in failure_markers):
                raise RuntimeError("Client startup failed; see client-smoke.log")
            if process.poll() is not None:
                raise RuntimeError(f"Client exited during startup with code {process.returncode}")
            if "textures/atlas/gui.png-atlas" in text:
                if ready_at is None:
                    ready_at = time.monotonic()
                if time.monotonic() - ready_at >= 15:
                    print("Fabric client completed resource startup and remained running for 15 seconds.")
                    break
            time.sleep(1)
        else:
            raise RuntimeError("Client did not finish resource startup within 240 seconds")
    finally:
        if process.poll() is None:
            os.killpg(process.pid, signal.SIGTERM)
            try:
                process.wait(timeout=10)
            except subprocess.TimeoutExpired:
                os.killpg(process.pid, signal.SIGKILL)
                process.wait()
        print(LOG.read_text(errors="replace")[-45000:])
