# Tài Liệu Dự Án NSO Client 217

## 📋 Tổng Quan Dự Án

**NSO Client 217** là một game client Java J2ME (Java Micro Edition) cho game Ninja School Online (NSO). Đây là phiên bản client chạy trên các thiết bị di động cũ hỗ trợ nền tảng J2ME/MIDP.

### Thông Tin Cơ Bản
- **Tên dự án**: NSO_217
- **Loại**: Java ME MIDlet (Mobile Game Client)
- **Platform**: J2ME/MIDP (Java Micro Edition)
- **Build system**: Apache Ant với NetBeans
- **Obfuscator**: ProGuard 7.6.0
- **Số lượng file Java**: 161 files
- **Output**: client_217.jar

### Đặc Điểm
- Game client 2D với đồ họa sprite-based
- Kết nối socket đến game server
- Hỗ trợ nhiều màn hình (login, game, chat, shop, inventory...)
- Có hệ thống auto-play/bot tích hợp
- Hỗ trợ cả touch và phím bấm
- Multi-resolution support với zoom levels

---

## 🔧 Yêu Cầu Hệ Thống

### Để Build Dự Án
- **JDK**: Java Development Kit 1.6 hoặc mới hơn
- **Apache Ant**: Build tool (thường đi kèm NetBeans)
- **NetBeans IDE**: Recommended IDE cho J2ME development
- **Wireless Toolkit (WTK)**: J2ME emulator và build tools
- **ProGuard**: Để obfuscate code (đã có trong thư mục proguard-7.6.0/)

### Để Chạy/Test
- **J2ME Emulator**: Như Sun Java Wireless Toolkit, hoặc
- **Thiết bị thực**: Điện thoại hỗ trợ J2ME/MIDP 2.0
- **KEmulator**: Emulator phổ biến để test J2ME games

---

## 📁 Cấu Trúc Thư Mục

```
NSO_217/
├── src/                          # Mã nguồn Java
│   ├── *.java                    # 161 file Java classes
│   ├── font/                     # Font resources
│   ├── x1/                       # Game resources (images, data)
│   │   ├── hd/                   # HD graphics
│   │   ├── del/                  # Deleted/unused resources
│   │   ├── e/                    # Effects
│   │   ├── font/                 # Font data
│   │   ├── u/                    # UI resources
│   │   ├── img/                  # Images
│   │   ├── eff/                  # Effect animations
│   │   ├── export/               # Exported data
│   │   ├── t/                    # Tiles
│   │   └── bg/                   # Backgrounds
│   └── map/                      # Map data
├── build/                        # Compiled classes (generated)
├── dist/                         # Distribution JAR (generated)
├── nbproject/                    # NetBeans project files
│   ├── build-impl.xml            # NetBeans build implementation
│   └── project.properties        # Project configuration
├── proguard-7.6.0/              # ProGuard obfuscator
├── build.xml                     # Ant build script
├── ProGuard.bat                  # Windows ProGuard script
├── ProGuardVIP.pro              # ProGuard configuration
└── NSO_217.jar                  # Compiled JAR file
```

---

## 🏗️ Kiến Trúc Code

### 1. Entry Point & Main Loop

#### GameMidlet.java
**Entry point** của ứng dụng J2ME MIDlet.

**Chức năng chính:**
- Khởi tạo ứng dụng MIDlet
- Load server configuration từ HTTPS hoặc manual input
- Đọc agent và provider info từ file resources
- Khởi tạo Session và Controller
- Start game loop qua MotherCanvas

**Key methods:**
```java
protected void startApp()        // Bắt đầu ứng dụng
protected void pauseApp()        // Tạm dừng
protected void destroyApp()      // Hủy ứng dụng
```

#### MotherCanvas.java
Thread chính chạy game loop.

#### GameCanvas.java
Main canvas cho rendering và input handling.

**Chức năng:**
- Quản lý input (keyboard, touch)
- Render game graphics
- Quản lý screens (currentScreen)
- Quản lý dialogs và menus
- Game tick và update loop

**Key properties:**
```java
public static mScreen currentScreen    // Screen hiện tại
public static GameCanvas instance      // Singleton instance
public static int w, h                 // Screen dimensions
public static boolean isTouch          // Touch support
public static Menu menu                // Game menu
```

### 2. Network Layer

#### Session_ME.java
Quản lý kết nối socket đến game server.

**Chức năng:**
- Kết nối socket (SocketConnection)
- Gửi/nhận messages qua DataOutputStream/DataInputStream
- Encryption với key-based system
- Thread riêng cho sender và receiver
- Auto reconnect

**Key methods:**
```java
public void gameAA(String host)     // Connect to server
public void sendMessage(Message)    // Gửi message
public boolean connected            // Connection status
```

#### Service.java
Service layer để gửi các request đến server.

**Chức năng:**
- Login, register
- Game actions (move, attack, use skill...)
- Chat, trade, party
- Shop, inventory operations
- Mọi interaction với server

**Pattern:**
```java
// Tạo message với command byte
Message msg = messageNotLogin((byte) -127);
msg.writer().writeUTF(username);
msg.writer().writeUTF(password);
session.sendMessage(msg);
```

#### Controller.java
Xử lý messages nhận từ server (message handler).

**Chức năng:**
- Parse messages từ server
- Update game state
- Dispatch events đến các screens
- Handle server responses

#### Message.java
Wrapper class cho network messages.

**Chức năng:**
- DataOutputStream writer
- DataInputStream reader
- Command byte
- Cleanup resources

#### UpdateServer.java
Load danh sách server từ URL hoặc manual input.

**Chức năng:**
```java
public static void a()              // Load từ HTTPS
public static void b1()             // Manual input IP
```

### 3. Screen Management

#### mScreen.java
Base class cho tất cả screens.

**Methods:**
```java
public void update()                // Update logic
public void paint(mGraphics g)     // Render
public void onPointerPressed()     // Touch input
public void onKeyPressed()         // Key input
```

#### Các Screen Classes

##### LoginScr.java
- Màn hình đăng nhập
- Input username/password
- Connect to server
- Login validation

##### SelectServerScr.java
- Chọn server
- Hiển thị server list
- Server status (online/offline)

##### SelectCharScr.java
- Chọn nhân vật
- Hiển thị char list
- Create new char
- Delete char

##### CreateCharScr.java
- Tạo nhân vật mới
- Chọn class (ninja type)
- Input char name

##### GameScr.java
**Main game screen** - màn hình chính khi chơi game.

**Chức năng chính:**
- Render game world (map, characters, mobs, effects)
- Camera management (cmx, cmy)
- Character movement và actions
- Chat system
- Inventory management
- Skill bar
- Menu system
- Auto-play integration

**Key vectors:**
```java
public static MyVector vCharInMap   // Nhân vật trong map
public static MyVector vMob         // Mob trong map
public static MyVector vNpc         // NPC trong map
public static MyVector vItemMap     // Item rơi trên map
public static MyVector vClan        // Clan members
public static MyVector vParty       // Party members
public static MyVector vFriend      // Friends list
```

##### SplashScr.java
- Splash screen khi start app
- Loading resources

##### MapScr.java
- Map selection/navigation screen

##### LanguageScr.java
- Chọn ngôn ngữ

##### RegisterScr.java
- Đăng ký tài khoản mới

### 4. Game Objects

#### Char.java
Class đại diện cho nhân vật người chơi.

**Properties:**
- ID, name, level, class
- HP, MP, stamina
- Position (x, y)
- Equipment (items body)
- Skills
- Stats (strength, agility, etc.)
- Clan, party info

#### MainObject.java
Base class cho các game objects có thể hiển thị effects.

**Chức năng:**
- Vector effects
- Render effects trên object

#### Mob.java
Class cho quái vật (monster/mob).

**Properties:**
- Template (MobTemplate)
- HP, max HP
- Level
- Position
- State (đang đứng, tấn công, chết...)

#### MobTemplate.java
Template data cho mob types.

**Data:**
- Name, description
- Graphics data
- Base stats

#### Npc.java
Class cho NPC (Non-Player Character).

**Properties:**
- Template (NpcTemplate)
- Position
- Dialog/quest data
- Shop data (nếu là shop NPC)

#### Item.java
Class cho items trong game.

**Properties:**
- Item ID
- Template (ItemTemplate)
- Quantity
- Options (item stats)
- Lock status
- Expire time

#### ItemTemplate.java
Template data cho item types.

**Data:**
- Name, description, icon
- Type (weapon, armor, consumable...)
- Level requirement
- Class requirement

#### Skill.java / Skills.java
Skill system.

**Skill.java**: Một skill instance
**Skills.java**: Quản lý danh sách skills

**Properties:**
- Skill ID, template
- Level, max level
- Cooldown
- Mana cost
- Damage/effect data

#### Effect.java / EffectData.java / EffectPaint.java
Hệ thống effects (visual effects).

**Effect.java**: Effect instances
**EffectData.java**: Effect data
**EffectPaint.java**: Effect rendering

**Types:**
- Skill effects
- Buff/debuff effects
- Environmental effects

#### Clan.java
Clan/Guild system.

**Data:**
- Clan name, icon
- Members list
- Clan level, exp

#### Party.java
Party system.

**Data:**
- Party members
- Leader
- Loot settings

#### Friend.java
Friend list system.

### 5. UI Components

#### Menu.java
Dropdown menu system.

#### Dialog.java
Base dialog class.

#### MsgDlg.java
Message dialog (popup messages).

#### InputDlg.java
Input dialog (single text input).

#### Input2Dlg.java
Two-input dialog.

#### InfoDlg.java
Information display dialog.

#### ChatPopup.java
Chat popup/bubble.

#### ChatTextField.java
Chat input field.

#### ChatManager.java / ChatTab.java
Chat system management.

**Chức năng:**
- Multiple chat tabs
- Chat history
- Filter chat types

#### TField.java
Text input field component.

#### Command.java / Command_Listener.java
Command pattern cho menu items và buttons.

#### Scroll.java / ScrollResult.java
Scrollable lists.

### 6. Graphics & Rendering

#### mGraphics.java
Wrapper cho J2ME Graphics API.

**Chức năng:**
- Draw images, text, shapes
- Clip regions
- Transform (scale, rotate)
- Zoom level support

#### mFont.java
Custom font rendering.

**Chức năng:**
- Render text với custom fonts
- Text measurement
- Multi-line text
- Color support

#### Paint.java
Painting/drawing utilities.

#### Image Management

##### MyImage.java
Image wrapper với caching.

##### ImageIcon.java
Icon images.

##### SmallImage.java
Small image sprites.

##### FrameImage.java
Animated frame sequences.

##### Frame.java
Single animation frame.

##### PartImage.java / Part.java / PartFrame.java
Character part system (body parts rendering).

**Chức năng:**
- Character sprite assembly
- Equipment visual
- Animation frames

#### TileMap.java
Tile-based map rendering.

**Chức năng:**
- Load map data
- Render tiles
- Collision detection
- Map layers

#### Arrow.java / Arrowpaint.java
Arrow/projectile rendering.

#### Lightning.java
Lightning effect rendering.

#### AnimateEffect.java
Animation system cho effects.

### 7. Auto-Play System

#### Auto.java
Main auto-play controller.

**Chức năng:**
- Auto attack
- Auto skill
- Auto pickup items
- Auto HP/MP potion
- Path finding

#### AutoNpc.java
Auto interact với NPC.

#### AutoNvhn.java
Auto chức năng đặc biệt (NVHN = Nhiệm Vụ Hằng Ngày?).

#### CheckManager.java
Check conditions cho auto actions.

#### As10.java / As20.java
Auto skill profiles hoặc auto settings.

### 8. Data Management

#### RMS.java
RMS (Record Management System) - J2ME persistent storage.

**Chức năng:**
- Save/load settings
- Save login info
- Save game preferences
- Cache data

**Key methods:**
```java
public static void saveRMSString(String key, String value)
public static String loadRMSString(String key)
public static void saveRMSInt(String key, int value)
public static int loadRMSInt(String key)
```

#### Res.java
Resource manager.

**Chức năng:**
- Load images
- Load data files
- Resource caching

#### mResources.java
Multi-language resources.

**Chức năng:**
- Load language strings
- Language switching
- Text formatting

#### GameData.java
Game data loader và parser.

**Chức năng:**
- Parse server data
- Cache game data
- Data validation

### 9. Utilities

#### MyVector.java
Custom Vector implementation (ArrayList-like).

**Methods:**
```java
public void addElement(Object o)
public Object elementAt(int index)
public void removeElement(Object o)
public void removeAllElements()
public int size()
```

#### mHashtable.java / mHashtable1.java
Custom Hashtable implementations.

#### NinjaUtil.java
Utility functions.

**Chức năng:**
- String utilities
- Number formatting
- Time formatting
- Distance calculation

#### Base64Utils.java
Base64 encoding/decoding.

#### Position.java / EPosition.java / BaseEPosition.java
Position/coordinate classes.

#### MovePoint.java
Movement path point.

#### Waypoint.java
Map waypoint system.

#### Timer.java
Game timer utilities.

### 10. Specific Game Systems

#### Task.java / TaskOrder.java
Quest/task system.

#### Buff.java
Buff/debuff system.

#### Stanima.java
Stamina system.

#### Ranked.java
PVP ranking system.

#### TuDanh.java
Tu Dành (special battle system?).

#### ChoBoss.java / PkBoss.java / PKBossS.java / TimBoss.java
Boss battle systems.

#### DoiKhu.java
Đổi khu (change zone) system.

#### Clan_ThanThu.java
Clan pet/summon system.

#### EggMonters.java
Pet egg/monster system.

#### Lanterns.java
Lantern event system.

#### ItemTree.java
Item tree (loot tree?) system.

#### StaticObj.java
Static map objects.

#### NextMap.java
Map transition system.

#### DunItem.java
Dungeon item system.

#### GoMap.java
Go to map function.

#### MobSoul.java
Mob soul system.

#### MonsterDart.java
Monster dart (pet system?).

#### nameDD.java
Name display system.

#### Hd9x.java
HD graphics support (9x resolution?).

#### LatHinh.java
Lat hinh (flip sprite?) system.

#### LockGame.java
Game lock/security system.

#### ReLogin.java
Re-login handling.

#### ConnectionMonitor.java
Monitor connection status.

#### SavePk.java
Save PK (Player Kill) data.

#### BuNhin.java
Bu Nhin (invisibility/stealth?) system.

#### TanSat.java
Tan sat (massacre/kill count?) system.

#### TaThu.java
Ta thu (pet collection?) system.

#### Domsang.java
Dom sang (lighting?) system.

#### cuong.java
Unknown utility (tên biến không rõ ràng).

#### Code.java / CodePhu.java
Code utilities hoặc cheat code system.

---

## 🔨 Hướng Dẫn Build

### Yêu Cầu Trước Khi Build
1. **Cài đặt JDK 1.6+**
2. **Cài đặt Apache Ant** (hoặc sử dụng NetBeans built-in)
3. **Cài đặt NetBeans IDE** với J2ME plugin
4. **Cài đặt Sun Java Wireless Toolkit (WTK)** hoặc tương đương

### Cách 1: Build với NetBeans

1. **Mở Project trong NetBeans:**
   ```
   File -> Open Project -> Chọn thư mục NSO_217
   ```

2. **Configure J2ME Platform:**
   - Right-click project -> Properties
   - Platform -> Chọn J2ME platform (CDC/CLDC)
   - Ensure MIDP 2.0 và CLDC 1.1 được chọn

3. **Build Project:**
   ```
   Right-click project -> Build
   ```
   Hoặc nhấn **F11**

4. **Output:**
   - JAR file: `dist/client_217.jar`
   - JAD file: `dist/client_217.jad`

### Cách 2: Build với Command Line (Apache Ant)

1. **Mở terminal trong thư mục project:**
   ```bash
   cd /path/to/NSO_217
   ```

2. **Build với Ant:**
   ```bash
   ant clean      # Clean build artifacts
   ant compile    # Compile Java sources
   ant jar        # Tạo JAR file
   ```

3. **Build tất cả:**
   ```bash
   ant clean jar
   ```

4. **Output:**
   - Compiled classes: `build/compiled/`
   - Final JAR: `dist/client_217.jar`
   - Final JAD: `dist/client_217.jad`

### Cách 3: Build với ProGuard (Obfuscate)

ProGuard được dùng để obfuscate code, làm khó đọc và giảm kích thước JAR.

1. **Build JAR thông thường trước:**
   ```bash
   ant clean jar
   ```

2. **Chạy ProGuard (Windows):**
   ```batch
   ProGuard.bat
   ```

3. **Hoặc chạy ProGuard manually:**
   ```bash
   cd proguard-7.6.0
   java -jar lib/proguard.jar @../ProGuardVIP.pro
   ```

4. **Configuration:**
   Xem file `ProGuardVIP.pro` để configure:
   - Input/output JARs
   - Keep classes (classes không bị obfuscate)
   - Optimization settings

### Build Options

**Trong `nbproject/project.properties`:**
```properties
# JAR compression
jar.compress=true

# Obfuscation
obfuscate=false

# JAR name
dist.jar=client_217.jar

# Manifest
manifest.midlets=MIDlet-1: NSO_217,/icon.png,GameMidlet
```

### Troubleshooting Build Issues

**Lỗi: "Cannot find J2ME platform"**
- Solution: Install Sun Java Wireless Toolkit hoặc configure platform path trong NetBeans

**Lỗi: "preverify failed"**
- Solution: Ensure J2ME preverify tool được cài đặt và trong PATH

**Lỗi: "Out of memory during compilation"**
- Solution: Tăng heap size cho Ant:
  ```bash
  export ANT_OPTS="-Xmx512m"
  ant jar
  ```

---

## 🚀 Hướng Dẫn Chạy & Test

### Cách 1: Chạy Trên Emulator (NetBeans)

1. **Run trong NetBeans:**
   ```
   Right-click project -> Run
   ```
   Hoặc nhấn **F6**

2. **NetBeans sẽ:**
   - Build project
   - Launch J2ME emulator
   - Install và run JAR file

### Cách 2: Chạy Với KEmulator

**KEmulator** là emulator phổ biến cho J2ME games.

1. **Download KEmulator:**
   - Website: http://kemulator.sourceforge.net/

2. **Install KEmulator:**
   - Extract files
   - Run KEmulator.exe (Windows) hoặc KEmulator.jar (Linux/Mac)

3. **Load JAR file:**
   - File -> Open -> Chọn `dist/client_217.jar`
   - Hoặc drag & drop JAR vào KEmulator

4. **Run game:**
   - Click Start

### Cách 3: Chạy Trên Thiết Bị Thực


1. **Transfer JAR file:**
   - Copy `dist/client_217.jar` và `dist/client_217.jad` vào thiết bị
   - Via Bluetooth, USB, hoặc download từ web

2. **Install:**
   - Open JAR file trên thiết bị
   - Phone sẽ hỏi confirm installation
   - Accept permissions

3. **Run:**
   - Tìm "NSO_217" trong menu applications
   - Launch game

### Configuration Trước Khi Chạy

**File cần configure trong resources:**

1. **agent.txt**
   - Chứa client agent string
   - Ví dụ: `"NSO_Client_217"`

2. **provider.txt**
   - Chứa provider ID (byte value)
   - Ví dụ: `0` hoặc `1`

### Emulator Controls

**Phím điều khiển trong emulator:**
- **Số 2, 4, 6, 8**: Di chuyển (lên, trái, phải, xuống)
- **Số 5 / Fire**: Select/OK
- **Soft keys**: Menu trái/phải
- **Số 0-9**: Shortcuts (tùy game)
- **#, ***: Extra functions

---

## 🛠️ Hướng Dẫn Development - Thêm Tính Năng Mới

### 1. Setup Development Environment

1. **Clone/Open project:**
   ```bash
   cd /path/to/NSO_217
   ```

2. **Open trong NetBeans:**
   ```
   File -> Open Project -> NSO_217
   ```

3. **Configure build:**
   - Ensure J2ME platform configured
   - Test build: `ant clean jar`

### 2. Hiểu Flow Chính Của Game

#### Game Startup Flow
```
GameMidlet.startApp()
    └─> MotherCanvas.start()
        └─> GameCanvas initialized
            └─> SplashScr shown
                └─> LanguageScr (chọn ngôn ngữ)
                    └─> LoginScr (đăng nhập)
                        └─> SelectCharScr (chọn char)
                            └─> GameScr (main game)
```

#### Network Communication Flow
```
User Action (ví dụ: click attack button)
    └─> GameScr handles input
        └─> Service.attackMob(mobId)
            └─> Create Message with command byte
                └─> Session_ME.sendMessage()
                    └─> Send via socket
                        
Server Response
    └─> Receive via Session_ME
        └─> Controller.onMessage(Message msg)
            └─> Parse command byte
                └─> Update game state (GameScr, Char, Mob, etc.)
                    └─> Render changes
```

#### Game Loop Flow
```
MotherCanvas.run() [main thread]
    └─> while(true):
        ├─> GameCanvas.update()        // Update logic
        │   └─> currentScreen.update()
        │       └─> GameScr.update()
        │           ├─> Update characters
        │           ├─> Update mobs
        │           ├─> Update effects
        │           └─> Camera movement
        │
        └─> GameCanvas.paint()         // Render
            └─> currentScreen.paint(g)
                └─> GameScr.paint(g)
                    ├─> Render map/tiles
                    ├─> Render characters
                    ├─> Render mobs
                    ├─> Render items
                    ├─> Render effects
                    └─> Render UI
```

### 3. Thêm Tính Năng: Ví Dụ Cụ Thể

#### Ví Dụ 1: Thêm Command Button Mới

**Scenario:** Thêm button "Teleport Home" trong game menu.

**Steps:**

1. **Tạo Command trong GameScr.java:**

```java
// Trong class GameScr
private Command cmdTeleportHome;

// Trong constructor hoặc init method
this.cmdTeleportHome = new Command("Về Nhà", new IActionListener() {
    public void perform() {
        // Gọi service để teleport
        Service.gI().requestTeleportHome();
    }
});
```

2. **Thêm vào Menu:**

```java
// Thêm command vào menu items
GameCanvas.menu.addItem(this.cmdTeleportHome);
```

3. **Implement Service Method (Service.java):**

```java
public final void requestTeleportHome() {
    try {
        Message msg = messageNotMap((byte) 10); // Command byte 10 cho teleport
        this.session.sendMessage(msg);
        msg.cleanup();
    } catch (IOException e) {
        e.printStackTrace();
    }
}
```

4. **Handle Server Response (Controller.java):**

```java
// Trong method xử lý messages
private static void handleTeleportResponse(Message msg) {
    try {
        byte result = msg.reader().readByte();
        if (result == 1) {
            // Teleport success
            GameCanvas.addNotify("Đã về nhà!", 2000, GameCanvas.NOTIFY_GREEN);
        } else {
            // Teleport failed
            GameCanvas.addNotify("Không thể về nhà!", 2000, GameCanvas.NOTIFY_RED);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
}
```

#### Ví Dụ 2: Thêm Auto Feature Mới

**Scenario:** Thêm tính năng auto buff mỗi 30 giây.

**Steps:**

1. **Tạo class AutoBuff.java:**

```java
public class AutoBuff {
    private static AutoBuff instance;
    public boolean isEnabled = false;
    private long lastBuffTime = 0;
    private static final long BUFF_INTERVAL = 30000; // 30 seconds
    
    public static AutoBuff gI() {
        if (instance == null) {
            instance = new AutoBuff();
        }
        return instance;
    }
    
    public void update() {
        if (!isEnabled) return;
        
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastBuffTime >= BUFF_INTERVAL) {
            // Use buff skill
            useBuffSkill();
            lastBuffTime = currentTime;
        }
    }
    
    private void useBuffSkill() {
        // Tìm skill buff trong danh sách skills
        for (int i = 0; i < GameScr.sks.length; i++) {
            SkillPaint skill = GameScr.sks[i];
            if (skill != null && skill.template.type == SkillTemplate.TYPE_BUFF) {
                // Sử dụng skill
                Service.gI().useSkill(skill.template.id);
                break;
            }
        }
    }
    
    public void toggle() {
        isEnabled = !isEnabled;
        GameCanvas.addNotify(
            "Auto Buff: " + (isEnabled ? "BẬT" : "TẮT"), 
            2000, 
            isEnabled ? GameCanvas.NOTIFY_GREEN : GameCanvas.NOTIFY_RED
        );
    }
}
```

2. **Integrate vào GameScr.java update loop:**

```java
// Trong GameScr.update()
public void update() {
    // ... existing update code ...
    
    // Update auto buff
    AutoBuff.gI().update();
    
    // ... rest of update code ...
}
```

3. **Thêm toggle button:**

```java
// Trong GameScr
private Command cmdToggleAutoBuff;

// Init
this.cmdToggleAutoBuff = new Command("Auto Buff", new IActionListener() {
    public void perform() {
        AutoBuff.gI().toggle();
    }
});

// Add to menu
GameCanvas.menu.addItem(this.cmdToggleAutoBuff);
```

#### Ví Dụ 3: Thêm Item Filter Cho Auto Pickup

**Scenario:** Filter items khi auto pickup, chỉ nhặt items quý.

**Steps:**

1. **Tạo ItemFilter.java:**

```java
public class ItemFilter {
    private static ItemFilter instance;
    public boolean enableFilter = false;
    private MyVector allowedItemTypes = new MyVector();
    
    public static ItemFilter gI() {
        if (instance == null) {
            instance = new ItemFilter();
        }
        return instance;
    }
    
    public void addAllowedType(int itemType) {
        if (!allowedItemTypes.contains(new Integer(itemType))) {
            allowedItemTypes.addElement(new Integer(itemType));
        }
    }
    
    public void removeAllowedType(int itemType) {
        allowedItemTypes.removeElement(new Integer(itemType));
    }
    
    public boolean shouldPickup(Item item) {
        if (!enableFilter) return true; // Pickup all if filter disabled
        
        // Check if item type is in allowed list
        for (int i = 0; i < allowedItemTypes.size(); i++) {
            int allowedType = ((Integer) allowedItemTypes.elementAt(i)).intValue();
            if (item.template.type == allowedType) {
                return true;
            }
        }
        
        // Check item rarity/quality
        if (item.template.level >= 50) { // High level items
            return true;
        }
        
        return false;
    }
}
```

2. **Modify Auto.java pickup logic:**

```java
// Trong Auto.java method pickup items
private void pickupItems() {
    for (int i = 0; i < GameScr.vItemMap.size(); i++) {
        ItemMap itemMap = (ItemMap) GameScr.vItemMap.elementAt(i);
        
        // Apply filter
        if (!ItemFilter.gI().shouldPickup(itemMap.item)) {
            continue; // Skip this item
        }
        
        // Existing pickup logic
        Service.gI().pickupItem(itemMap.id);
    }
}
```

3. **Add configuration UI:**

```java
// Create dialog to configure filter
private void showItemFilterConfig() {
    InputDlg dlg = new InputDlg();
    dlg.setInfo("Item Filter", new Command[] {
        new Command("Weapon", new IActionListener() {
            public void perform() {
                ItemFilter.gI().addAllowedType(ItemTemplate.TYPE_WEAPON);
            }
        }),
        new Command("Armor", new IActionListener() {
            public void perform() {
                ItemFilter.gI().addAllowedType(ItemTemplate.TYPE_ARMOR);
            }
        }),
        // ... more item types
    }, null);
    GameCanvas.currentDialog = dlg;
}
```

### 4. Common Patterns & Best Practices

#### Pattern 1: Adding Network Commands

**Template:**
```java
// 1. Define command trong Service.java
public final void yourNewCommand(int param1, String param2) {
    try {
        Message msg = messageNotMap((byte) YOUR_CMD_BYTE);
        msg.writer().writeInt(param1);
        msg.writer().writeUTF(param2);
        this.session.sendMessage(msg);
        msg.cleanup();
    } catch (IOException e) {
        e.printStackTrace();
    }
}

// 2. Handle response trong Controller.java
private static void handleYourResponse(Message msg) {
    try {
        // Parse response data
        int responseCode = msg.reader().readInt();
        String data = msg.reader().readUTF();
        
        // Update game state
        // ...
        
    } catch (Exception e) {
        e.printStackTrace();
    }
}
```

#### Pattern 2: Adding UI Elements

**Template:**
```java
// 1. Create command
Command cmd = new Command("Label", new IActionListener() {
    public void perform() {
        // Action code
    }
});

// 2. Add to appropriate place
GameCanvas.menu.addItem(cmd);        // Main menu
// hoặc
someDialog.addCommand(cmd);           // Dialog
// hoặc
someScreen.setCommands(new Command[] {cmd1, cmd2}); // Screen
```

#### Pattern 3: Adding Game Objects

**Template:**
```java
// 1. Extend appropriate base class
public class MyNewObject extends MainObject {
    public int x, y;
    public int customProperty;
    
    public void update() {
        // Update logic
    }
    
    public void paint(mGraphics g) {
        // Render logic
        g.drawImage(myImage, x, y, 0);
    }
}

// 2. Add to appropriate vector trong GameScr
MyNewObject obj = new MyNewObject();
GameScr.vMyObjects.addElement(obj);

// 3. Update & paint trong GameScr loop
// In GameScr.update():
for (int i = 0; i < vMyObjects.size(); i++) {
    ((MyNewObject) vMyObjects.elementAt(i)).update();
}

// In GameScr.paint():
for (int i = 0; i < vMyObjects.size(); i++) {
    ((MyNewObject) vMyObjects.elementAt(i)).paint(g);
}
```

### 5. Debugging Tips

#### Enable Debug Logging

**Add log statements:**
```java
System.out.println("DEBUG: Variable value = " + value);
System.out.println("DEBUG: Entering method XYZ");
```

**View logs:**
- NetBeans: Output window
- KEmulator: Console window
- Device: Depends on device logger

#### Common Debug Techniques

1. **Check Network Messages:**
```java
// Trong Session_ME.java, add logging
public void sendMessage(Message msg) {
    System.out.println("SEND CMD: " + msg.command);
    // ... existing code
}
```

2. **Visual Debugging:**
```java
// Draw debug info on screen
public void paint(mGraphics g) {
    // ... existing paint code ...
    
    // Debug overlay
    g.setColor(0xFF0000); // Red
    g.drawString("FPS: " + currentFPS, 10, 10, 0);
    g.drawString("Pos: " + Char.myChar().x + "," + Char.myChar().y, 10, 25, 0);
}
```

3. **Breakpoint Simulation:**
```java
// Pause execution để check state
if (someCondition) {
    System.out.println("BREAKPOINT: Check values");
    System.out.println("Value1: " + val1);
    System.out.println("Value2: " + val2);
    // Add delay để observe
    try { Thread.sleep(2000); } catch(Exception e) {}
}
```

### 6. Testing Your Changes

#### Unit Testing Workflow

1. **Make changes**
2. **Build:** `ant clean jar`
3. **Run in emulator**
4. **Test functionality:**
   - Normal flow
   - Edge cases
   - Error conditions
5. **Check for:**
   - NullPointerExceptions
   - ArrayIndexOutOfBoundsException
   - Memory leaks (objects not cleaned up)
   - Network timeouts

#### Test Checklist

- [ ] Code compiles without errors
- [ ] No warnings (hoặc document why warning is OK)
- [ ] Feature works as expected
- [ ] Doesn't break existing features
- [ ] UI is responsive
- [ ] Network commands work
- [ ] Auto-save/RMS works if applicable
- [ ] Memory usage acceptable
- [ ] Performance acceptable (no lag)

### 7. Code Conventions

**Naming conventions trong project:**
- Class names: `PascalCase` (ví dụ: `GameScr`, `MainObject`)
- Method names: `camelCase` (ví dụ: `updatePosition`, `paintEffect`)
- Obfuscated methods: `gameAA`, `gameAB`, etc. (do ProGuard)
- Static instances: `instance` với `gI()` getter
- Constants: `UPPER_SNAKE_CASE` (ví dụ: `MAX_LEVEL`)

**Common patterns:**
```java
// Singleton pattern
public class MyClass {
    private static MyClass instance;
    
    public static MyClass gI() {
        if (instance == null) {
            instance = new MyClass();
        }
        return instance;
    }
}

// Vector iteration
for (int i = 0; i < myVector.size(); i++) {
    Object obj = myVector.elementAt(i);
    if (obj != null) {
        // Process obj
    }
}

// Safe cleanup
try {
    // Risky code
} catch (Exception e) {
    e.printStackTrace();
} finally {
    // Cleanup code
}
```

### 8. Performance Tips

**J2ME Performance Best Practices:**

1. **Minimize Object Creation:**
   ```java
   // BAD
   for (int i = 0; i < 100; i++) {
       String s = new String("text");
   }
   
   // GOOD
   String s = "text";
   for (int i = 0; i < 100; i++) {
       // Use s
   }
   ```

2. **Reuse Objects:**
   ```java
   // Reuse Message objects với cleanup()
   Message msg = new Message((byte) 1);
   // ... use msg
   msg.cleanup(); // Clear for reuse
   ```

3. **Efficient Rendering:**
   ```java
   // Only paint visible objects
   if (obj.x >= cmx - 50 && obj.x <= cmx + GameCanvas.w + 50) {
       obj.paint(g);
   }
   ```

4. **Avoid String Concatenation in Loops:**
   ```java
   // BAD
   String result = "";
   for (int i = 0; i < 100; i++) {
       result += "text";
   }
   
   // GOOD
   StringBuffer sb = new StringBuffer();
   for (int i = 0; i < 100; i++) {
       sb.append("text");
   }
   String result = sb.toString();
   ```

---

## 📝 Configuration Files

### nbproject/project.properties

**Key properties:**

```properties
# JAR output name
dist.jar=client_217.jar

# Compression
jar.compress=true

# Obfuscation
obfuscate=false

# MIDlet info
manifest.midlets=MIDlet-1: NSO_217,/icon.png,GameMidlet
manifest.others=MIDlet-Vendor: NSO_217\nMIDlet-Version: 1.0\n
```

### ProGuardVIP.pro


**ProGuard configuration cho obfuscation:**

```properties
# Input/output
-injars dist/client_217.jar
-outjars dist/client_217_obf.jar

# Keep MIDlet entry point
-keep public class GameMidlet

# Keep public methods used by J2ME framework
-keepclasseswithmembers class * {
    public static void main(java.lang.String[]);
}

# Optimization
-optimizationpasses 5
-optimizations !code/simplification/arithmetic

# Don't warn about missing J2ME classes
-dontwarn javax.microedition.**
```

### Resource Files

**src/agent.txt**
- Client agent identification string

**src/provider.txt**
- Provider ID (byte value)

**src/icon.png**
- Application icon

---

## 🌐 Network Protocol

### Message Structure

**Messages gửi/nhận qua socket:**

```
[1 byte]  - Command byte
[n bytes] - Data (encoded theo command)
```

### Common Command Bytes

**Client -> Server:**
```java
-127  // Login
-126  // Select character
-125  // Set client type
-30   // Sub-commands
-29   // Not login commands
-28   // Not map commands
```

**Server -> Client:**
```java
// Parse trong Controller.java
// Mỗi command byte có handler riêng
```

### Message Reading/Writing

**Writing data:**
```java
Message msg = new Message((byte) CMD);
msg.writer().writeByte(value);
msg.writer().writeShort(value);
msg.writer().writeInt(value);
msg.writer().writeLong(value);
msg.writer().writeUTF(string);
msg.writer().writeBoolean(flag);
session.sendMessage(msg);
msg.cleanup();
```

**Reading data:**
```java
byte b = msg.reader().readByte();
short s = msg.reader().readShort();
int i = msg.reader().readInt();
long l = msg.reader().readLong();
String str = msg.reader().readUTF();
boolean flag = msg.reader().readBoolean();
```

### Encryption

Session sử dụng key-based encryption:
```java
public byte[] key = null;
```

Key được exchange sau khi connect thành công.

---

## 🐛 Troubleshooting

### Build Issues

**Problem: "Cannot find J2ME platform"**
```
Solution:
1. Install Sun Java Wireless Toolkit
2. NetBeans -> Tools -> Java Platforms
3. Add platform -> J2ME MIDP Platform
```

**Problem: "Preverify failed"**
```
Solution:
- Ensure preverify tool in PATH
- Check platform configuration
- Try clean build: ant clean jar
```

**Problem: "Out of memory"**
```
Solution:
export ANT_OPTS="-Xmx1024m"
ant clean jar
```

### Runtime Issues

**Problem: "Cannot connect to server"**
```
Solution:
1. Check IP và PORT trong UpdateServer.java
2. Ensure server đang chạy
3. Check firewall settings
4. Test connectivity: telnet IP PORT
```

**Problem: "NullPointerException on startup"**
```
Solution:
1. Check resource files exist (agent.txt, provider.txt)
2. Check image resources loaded correctly
3. Enable debug logging để trace
```

**Problem: "Graphics không hiển thị"**
```
Solution:
1. Check image paths correct
2. Ensure images trong resources folder
3. Try different emulator
4. Check lowGraphic setting
```

### Emulator Issues

**Problem: "JAR không chạy trong KEmulator"**
```
Solution:
1. Ensure JAR & JAD files có
2. Check manifest.mf settings
3. Try different emulator version
4. Rebuild với jar.compress=false
```

**Problem: "Touch không hoạt động"**
```
Solution:
1. Enable touch trong emulator settings
2. Check GameCanvas.isTouch flag
3. Test với phím thay vì touch
```

---

## 💡 Tips & Best Practices

### Development Tips

1. **Use RMS để save settings:**
   ```java
   RMS.saveRMSString("mykey", "value");
   String value = RMS.loadRMSString("mykey");
   ```

2. **Log debugging info:**
   ```java
   System.out.println("DEBUG: " + info);
   ```

3. **Test trên nhiều emulator:**
   - KEmulator
   - Sun WTK emulator
   - Real device nếu có

4. **Optimize performance:**
   - Minimize object creation
   - Reuse objects
   - Efficient rendering (only visible objects)
   - Cache expensive calculations

5. **Handle exceptions:**
   ```java
   try {
       // Risky code
   } catch (Exception e) {
       e.printStackTrace();
       // Fallback behavior
   }
   ```

### Code Organization

1. **Singleton pattern cho managers:**
   ```java
   private static MyManager instance;
   public static MyManager gI() {
       if (instance == null) instance = new MyManager();
       return instance;
   }
   ```

2. **Use MyVector thay vì arrays cho dynamic lists**

3. **Cleanup resources:**
   ```java
   msg.cleanup();
   image.recycle();
   vector.removeAllElements();
   ```

4. **Separate concerns:**
   - Network code trong Service
   - UI code trong Screens
   - Game logic trong GameScr
   - Rendering trong Paint classes

### Testing Workflow

1. **Local testing:**
   ```bash
   ant clean jar
   # Run trong emulator
   ```

2. **Test checklist:**
   - [ ] Normal flow works
   - [ ] Edge cases handled
   - [ ] No memory leaks
   - [ ] Performance acceptable
   - [ ] No exceptions trong log
   - [ ] Network commands work
   - [ ] UI responsive

3. **Device testing:**
   - Transfer JAR to device
   - Test với real network
   - Test với low memory
   - Test touch & keypad input

---

## ❓ FAQ

### General Questions

**Q: Dự án này chạy trên platform nào?**
A: Java ME (J2ME) - MIDP 2.0, CLDC 1.1. Chạy trên điện thoại di động cũ hỗ trợ J2ME.

**Q: Có thể chuyển sang Android/iOS không?**
A: Cần port lại hoàn toàn. J2ME API khác hoàn toàn với Android/iOS. Có thể sử dụng framework như libGDX hoặc viết lại native.

**Q: Server code ở đâu?**
A: Đây chỉ là client code. Server code riêng biệt (không có trong repo này).

**Q: Làm sao để thay đổi server IP?**
A: Modify `UpdateServer.java` - method `a()` (HTTPS load) hoặc `b1()` (manual input).

### Development Questions

**Q: Làm sao để add new screen?**
A: 
1. Extend `mScreen` class
2. Implement `update()` và `paint()` methods
3. Set `GameCanvas.currentScreen = new YourScreen()`

**Q: Làm sao để gửi message đến server?**
A:
1. Add method trong `Service.java`
2. Create Message với command byte
3. Write data với `msg.writer()`
4. Send với `session.sendMessage(msg)`

**Q: Làm sao để handle server response?**
A:
1. Add handler trong `Controller.java`
2. Parse message với `msg.reader()`
3. Update game state
4. Trigger UI update

**Q: Game loop chạy ở đâu?**
A: `MotherCanvas.run()` - main thread chạy update & paint loop.

**Q: Làm sao để add auto feature?**
A:
1. Create auto class
2. Add update logic
3. Call trong `GameScr.update()`
4. Add toggle command

### Technical Questions

**Q: ProGuard có bắt buộc không?**
A: Không. ProGuard chỉ để obfuscate code và giảm size. Development có thể skip.

**Q: Tại sao có nhiều method tên gameAA, gameAB...?**
A: Code đã bị obfuscate bởi ProGuard. Đây là tên sau khi obfuscate.

**Q: RMS là gì?**
A: Record Management System - persistent storage của J2ME (tương tự SharedPreferences Android).

**Q: Làm sao để debug network messages?**
A: Add logging trong `Session_ME.java` send/receive methods:
```java
System.out.println("SEND: " + msg.command);
System.out.println("RECV: " + msg.command);
```

**Q: Memory limit của J2ME?**
A: Thường 2-8MB tùy device. Cần optimize memory usage rất kỹ.

---

## 📚 Resources

### J2ME Development

- **Sun Java Wireless Toolkit**: Official J2ME development kit
- **KEmulator**: Popular J2ME emulator
- **NetBeans**: IDE với J2ME support tốt

### Documentation

- **MIDP 2.0 API**: https://docs.oracle.com/javame/config/cldc/ref-impl/midp2.0/jsr118/
- **CLDC 1.1 API**: https://docs.oracle.com/javame/config/cldc/ref-impl/cldc1.1/jsr139/

### Tools

- **ProGuard**: Code obfuscation
- **Apache Ant**: Build automation
- **Git**: Version control (recommended)

---

## 📝 Kết Luận

Tài liệu này cung cấp cái nhìn tổng quan và chi tiết về dự án NSO Client 217:

✅ **Đã cover:**
- Tổng quan kiến trúc hệ thống
- Cấu trúc code và các class chính
- Hướng dẫn build & run chi tiết
- Hướng dẫn develop thêm tính năng với examples cụ thể
- Debugging và troubleshooting
- Best practices và tips

🎯 **Để bắt đầu dev:**
1. Setup environment (JDK, NetBeans, WTK)
2. Build project: `ant clean jar`
3. Run trong emulator để test
4. Đọc code trong `GameScr.java`, `Service.java`, `Controller.java`
5. Tham khảo examples trong phần "Hướng Dẫn Development"
6. Test thoroughly trước khi release

🔧 **Next Steps:**
- Tạo CLAUDE.md cho project-specific conventions
- Setup git repository (nếu chưa có)
- Document network protocol chi tiết hơn
- Create automated tests
- Setup CI/CD pipeline

💬 **Support:**
Nếu có câu hỏi hoặc cần help thêm, refer back to document này hoặc explore code trực tiếp.

---

**Document Version**: 1.0  
**Last Updated**: 2026-08-17  
**Author**: Generated by Claude Code  
**Project**: NSO_217 Client
