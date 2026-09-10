/** Uses the built Session_ME to verify the runtime-specific reconnect hook. */
public final class SessionReconnectRouteTest {
    public static void main(String[] args) {
        Session_ME session = Session_ME.gI();
        session.fieldAD();
        boolean taThu = "ta-thu".equals(args[0]);
        if (AccountAutoManager.calls != (taThu ? 0 : 1)
                || TaThuAccountManager.calls != (taThu ? 1 : 0)
                || Session_ME.gameAP) {
            throw new AssertionError("wrong manager or legacy ReLogin started");
        }
        System.out.println("PASS " + args[0] + ": Session_ME routes reconnect to the correct manager");
    }
}
class AccountAutoManager {
    static int calls;
    public static boolean onReconnectRequested() { ++calls; return true; }
}
class TaThuAccountManager {
    static int calls;
    public static boolean onReconnectRequested() { ++calls; return true; }
}
