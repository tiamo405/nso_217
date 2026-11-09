/**
 * Một nhiệm vụ chính tuyến (ctaskId). Mỗi nhiệm vụ là một file riêng trong
 * uplevel35/src/tasks/ để sửa lỗi độc lập.
 *
 * As35 lo phần chung:
 *  - chưa có nhiệm vụ (taskMaint == null)  -> đi gặp NPC nhận nhiệm vụ;
 *  - đã ở bước cuối (index == subNames-1) -> đi gặp NPC trả nhiệm vụ.
 * Lớp con chỉ xử lý các bước ở giữa trong tick().
 */
abstract class Uplevel35Task {
    /** ctaskId mà server gửi về cho nhiệm vụ này. */
    abstract int taskId();

    /** Tên ngắn gọn để in log. */
    abstract String label();

    /**
     * Gọi mỗi tick (~100ms) khi đang làm bước task.index (chưa phải bước cuối).
     * Không được block lâu; trả về sau mỗi hành động.
     */
    abstract void tick(As35 bot, Char me, Task task);

    /**
     * Có được rời map hiện tại để đi mua thức ăn / Nấm / Mũ Noel không. Map ẩn (vào bằng NPC
     * hoặc chìa khóa) không có trong đồ thị đường đi: rời đi sẽ kẹt -> đăng nhập lại -> mất tiến độ.
     */
    boolean canLeaveMap(int mapId) {
        return true;
    }

    /** Reset trạng thái nội bộ khi nhận nhiệm vụ mới / đăng nhập lại. */
    void reset() {
    }
}
