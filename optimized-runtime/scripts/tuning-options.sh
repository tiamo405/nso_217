#!/usr/bin/env bash
# Script cấu hình tham số cho optimized workers
export JAVA_BIN=${JAVA_BIN:-java}
export JAVA_XMS=${JAVA_XMS:-8m}
export JAVA_XMX=${JAVA_XMX:-36m}

# Tối ưu Garbage Collector và Thread Stack của JVM:
# Sử dụng SerialGC cho bot nhẹ, giải phóng heap ngay lập tức
# -Xss256k: giảm stack size mỗi thread từ 1024k xuống 256k -> tiết kiệm 6-8MB RAM thực mỗi worker
# -XX:CICompilerCount=2: giảm số luồng biên dịch JIT chạy ngầm
export JAVA_OPTS=${JAVA_OPTS:-"-XX:+UseSerialGC -XX:MinHeapFreeRatio=5 -XX:MaxHeapFreeRatio=10 -Djava.awt.headless=true -Xss256k -XX:CICompilerCount=2"}

# Các tham số tinh chỉnh hiệu năng NSO Optimized Runtime:
# - nso.tick.ms: 80ms (12.5 FPS) - khớp với luồng combat auto 100ms
# - nso.skip.paint: true - triệt tiêu toàn bộ lệnh vẽ màn hình
# - nso.skip.decorations: true - bỏ qua mọi hiệu ứng hình ảnh, đèn lồng, quái bay
# - nso.lazy.map: true - chỉ tải map khi nhân vật di chuyển tới
# - nso.event.sender: true - chờ tín hiệu packet thay vì spin wait 10ms
OPTIMIZED_SYSTEM_PROPS=(
  "-Dnso.optimized=true"
  "-Dnso.tick.ms=${NSO_TICK_MS:-100}"
  "-Dnso.skip.paint=${NSO_SKIP_PAINT:-true}"
  "-Dnso.skip.periodic.gc=${NSO_SKIP_PERIODIC_GC:-true}"
  "-Dnso.skip.auto.popup=${NSO_SKIP_AUTO_POPUP:-true}"
  "-Dnso.skip.decorations=${NSO_SKIP_DECORATIONS:-true}"
  "-Dnso.lazy.map=${NSO_LAZY_MAP:-true}"
  "-Dnso.event.sender=true"
  "-Dnso.nvhn.headless=true"
)
