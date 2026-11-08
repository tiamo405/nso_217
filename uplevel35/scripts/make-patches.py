"""Tạo patch uplevel35 từ src/ (chỉ đọc src/, ghi patch vào uplevel35/patches)."""
import difflib
import pathlib
import sys

repo = pathlib.Path(sys.argv[1])
out = repo / "uplevel35" / "patches"
out.mkdir(parents=True, exist_ok=True)


def make(name, filename, transform):
    old = (repo / "src" / filename).read_text(encoding="utf-8")
    new = transform(old)
    diff = difflib.unified_diff(
        old.splitlines(keepends=True), new.splitlines(keepends=True),
        fromfile="a/" + filename, tofile="b/" + filename, n=3)
    (out / name).write_text("".join(diff), encoding="utf-8")
    print("wrote", name)


def account(s):
    old = '''        if (me.clevel < 30) {
            skipCurrentCharacter("nhân vật " + me.cName + " level=" + me.clevel + " < 30, bỏ qua");
            return;
        }
        System.out.println("AUTO NVHN: chuẩn bị nhân vật " + me.cName);
        AutoPrepareNvhn prepare = new AutoPrepareNvhn();
        prepare.fieldAD();
        Code.fieldAA((Auto) prepare);
    }
'''
    new = '''        if (!Uplevel35.inLevelRange(me.clevel)) {
            skipCurrentCharacter("UPLEVEL35: nhân vật " + me.cName + " level=" + me.clevel
                    + " ngoài khoảng " + Uplevel35.MIN_LEVEL + ".." + (Uplevel35.TARGET_LEVEL - 1) + ", bỏ qua");
            return;
        }
        System.out.println("UPLEVEL35: bắt đầu nhân vật " + me.cName + " level=" + me.clevel);
        Code.fieldAA((Auto) new As35());
    }

    /** As35 báo xong nhân vật (đủ level hoặc gặp nhiệm vụ chưa hỗ trợ). */
    public static synchronized void onUplevel35Finished(String reason) {
        if (!enabled || switching) {
            return;
        }
        skipCurrentCharacter(reason);
    }
'''
    assert s.count(old) == 1
    s = s.replace(old, new)
    header_old = '        if (username.equalsIgnoreCase("username") && password.equalsIgnoreCase("password")) {\n'
    header_new = ('        if ((username.equalsIgnoreCase("username") || username.equalsIgnoreCase("account"))\n'
                  '                && password.equalsIgnoreCase("password")) {\n')
    assert s.count(header_old) == 1
    return s.replace(header_old, header_new)


def controller(s):
    old = 'ChatPopup.gameAA(utf13 = fieldAB.reader().readUTF(), var78);\n'
    assert s.count(old) == 1
    i = s.index(old)
    indent = s[s.rfind('\n', 0, i) + 1:i]
    return s.replace(old, old + indent + 'Uplevel35.onNpcMessage(var78.template.npcTemplateId, utf13);\n')


def skill_rms(s):
    old1 = '''                        if (Char.getMyChar().gameBA()) {
                            RMS.gameAA("skill", var59);
                        } else {
                            RMS.gameAA("skillnhanban", var59);
                        }
'''
    new1 = '''                        // Packet skill có thể đến trước khi isHuman đồng bộ: lưu cả hai khóa.
                        RMS.gameAA("skill", var59);
                        RMS.gameAA("skillnhanban", var59);
'''
    old2 = '''                    if (Char.getMyChar().gameBA()) {
                        gameAB(new DataInputStream(new ByteArrayInputStream(RMS.gameAA("skill"))));
                    } else {
                        gameAB(new DataInputStream(new ByteArrayInputStream(RMS.gameAA("skillnhanban"))));
                    }
'''
    new2 = '''                    String skillRmsKey = Char.getMyChar().gameBA() ? "skill" : "skillnhanban";
                    byte[] skillRmsData = RMS.gameAA(skillRmsKey);
                    if (skillRmsData != null && skillRmsData.length > 0) {
                        gameAB(new DataInputStream(new ByteArrayInputStream(skillRmsData)));
                    } else {
                        System.out.println("UPLEVEL35 WARN: thiếu RMS " + skillRmsKey + ", yêu cầu tải lại skill");
                        GameScr.vcSkill = -1;
                        Service.gI().updateSkill();
                    }
'''
    assert s.count(old1) == 1 and s.count(old2) == 1
    return s.replace(old1, new1).replace(old2, new2)


make("001-accountmanager-uplevel35.patch", "AccountAutoManager.java", account)
make("003-controller-skill-rms.patch", "Controller.java", skill_rms)
make("002-controller-npc-message.patch", "Controller.java", controller)
