#!/usr/bin/env bash
set -euo pipefail

RUNTIME_DIR=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)
REPO_DIR=$(cd -- "$RUNTIME_DIR/.." && pwd)
BUILD_ROOT=$(realpath -m -- "${TA_THU_BUILD_DIR:-$RUNTIME_DIR/build}")
ACCOUNT_CSV=${TA_THU_ACCOUNT_CSV:-"$REPO_DIR/account.csv"}

if [[ "$BUILD_ROOT" == / || "$BUILD_ROOT" == "$RUNTIME_DIR" || "$BUILD_ROOT" == "$REPO_DIR" ]]; then
    echo "TA_THU_BUILD_DIR không an toàn: $BUILD_ROOT" >&2
    exit 1
fi

build_parent=$(dirname -- "$BUILD_ROOT")
build_name=$(basename -- "$BUILD_ROOT")
mkdir -p "$build_parent"
STAGING_DIR=$(mktemp -d "$build_parent/.${build_name}.build.XXXXXX")
CLASSES_DIR="$STAGING_DIR/classes"
WORK_SRC_DIR="$STAGING_DIR/src-repo"
SOURCES_FILE="$STAGING_DIR/sources.txt"
BACKUP_DIR="$build_parent/.${build_name}.old.$$"

cleanup() {
    rm -rf -- "$STAGING_DIR" "$BACKUP_DIR"
}
trap cleanup EXIT

mkdir -p "$CLASSES_DIR" "$WORK_SRC_DIR"
cp -R "$REPO_DIR/src"/. "$WORK_SRC_DIR"/
cp -R "$RUNTIME_DIR/src"/. "$WORK_SRC_DIR"/
cp -R "$RUNTIME_DIR/overrides"/. "$WORK_SRC_DIR"/

# Apply Ta Thu-only hooks to the disposable source copy. Repository src/ is untouched.
sed -i 's/AccountAutoManager\.start()/TaThuAccountManager.start()/' "$WORK_SRC_DIR/GameMidlet.java"
sed -i 's/AccountAutoManager\.onDisconnected()/TaThuAccountManager.onDisconnected()/' "$WORK_SRC_DIR/Controller.java"
sed -i 's/AccountAutoManager\.onServerMessage(utf)/TaThuAccountManager.onServerMessage(utf)/' "$WORK_SRC_DIR/Controller.java"
sed -i 's/AccountAutoManager\.onCaveEntered()/TaThuAccountManager.onCaveEntered()/g' "$WORK_SRC_DIR/Controller.java" "$WORK_SRC_DIR/AutoEnterCave.java"
sed -i 's/AccountAutoManager\.onCharacterBelowLevel30(utf13)/TaThuAccountManager.onCharacterBelowLevel30(utf13)/' "$WORK_SRC_DIR/Controller.java"
sed -i 's/AccountAutoManager\.onGameReady()/TaThuAccountManager.onGameReady()/' "$WORK_SRC_DIR/Controller.java"
sed -i 's/AccountAutoManager\.onCharacterList(var49.name)/TaThuAccountManager.onCharacterList(var49.name)/' "$WORK_SRC_DIR/Controller.java"
sed -i '/ChatPopup.gameAA(utf13 = fieldAB.reader().readUTF(), var78);/a\                            TaThuAccountManager.onNpcMessage(var78.template.npcTemplateId, utf13);' "$WORK_SRC_DIR/Controller.java"
sed -i '/var87.count = fieldAB.reader().readInt();/a\                            TaThuAccountManager.onTaskOrderProgress(var87);' "$WORK_SRC_DIR/Controller.java"
sed -i '/LockGame.fieldAN();/a\                    TaThuAccountManager.onTaskOrderRemoved(var85);' "$WORK_SRC_DIR/Controller.java"
sed -i 's/Code\.fieldAD();/TaThuAccountManager.onPreparationFinished();/' "$WORK_SRC_DIR/AutoPrepareNvhn.java"
sed -i 's/AccountAutoManager\.onPostDailyFlipFinished()/TaThuAccountManager.onPostDailyFlipFinished()/' "$WORK_SRC_DIR/AutoFlipNvhn.java"
sed -i '/CodePhu\.fieldAA(var0);/a\            TaThuAccountManager.onServerMessage(var0);' "$WORK_SRC_DIR/InfoMe.java"

# Apply patches
if [[ -d "$RUNTIME_DIR/patches" ]]; then
    for patch in "$RUNTIME_DIR/patches"/*.patch; do
        if [[ -f "$patch" ]]; then
            echo "Applying patch: $(basename "$patch")"
            if ! patch --batch --forward -d "$WORK_SRC_DIR" -p1 < "$patch"; then
                echo "Build lỗi: không áp dụng được patch $(basename "$patch")" >&2
                exit 1
            fi
        fi
    done
fi

require_hook() {
    local pattern=$1
    local file=$2
    if ! grep -Fq -- "$pattern" "$file"; then
        echo "Build lỗi: không chèn được hook '$pattern' vào $(basename -- "$file")" >&2
        exit 1
    fi
}

require_hook 'TaThuAccountManager.start()' "$WORK_SRC_DIR/GameMidlet.java"
require_hook 'TaThuAccountManager.onDisconnected()' "$WORK_SRC_DIR/Controller.java"
require_hook 'TaThuAccountManager.onNpcMessage(' "$WORK_SRC_DIR/Controller.java"
require_hook 'TaThuAccountManager.onServerMessage(' "$WORK_SRC_DIR/InfoMe.java"
require_hook 'TaThuAccountManager.onGameReady()' "$WORK_SRC_DIR/Controller.java"
require_hook 'TaThuAccountManager.onCharacterList(' "$WORK_SRC_DIR/Controller.java"
require_hook 'TaThuAccountManager.onTaskOrderProgress(' "$WORK_SRC_DIR/Controller.java"
require_hook 'TaThuAccountManager.onTaskOrderRemoved(' "$WORK_SRC_DIR/Controller.java"
require_hook 'TaThuAccountManager.onPreparationFinished()' "$WORK_SRC_DIR/AutoPrepareNvhn.java"
require_hook 'TaThuAccountManager.onPostDailyFlipFinished()' "$WORK_SRC_DIR/AutoFlipNvhn.java"
require_hook 'TaThuAccountManager.onCaveEntered()' "$WORK_SRC_DIR/AutoEnterCave.java"

# The desktop JVM needs class-based resource lookup for these decompiled expressions.
sed -i 's#"".getClass().getResourceAsStream("/map/" + var1)#TileMap.class.getResourceAsStream("/map/" + var1)#' "$WORK_SRC_DIR/TileMap.java"
sed -i 's#"".getClass().getResourceAsStream(var0)#RMS.class.getResourceAsStream(var0)#' "$WORK_SRC_DIR/RMS.java"
sed -i 's#"".getClass().getResourceAsStream(var0)#Res.class.getResourceAsStream(var0)#' "$WORK_SRC_DIR/Res.java"

find "$REPO_DIR/headless-runtime/src" "$WORK_SRC_DIR" -name '*.java' | sort >"$SOURCES_FILE"
javac -encoding UTF-8 -source 8 -target 8 -Xlint:none -d "$CLASSES_DIR" @"$SOURCES_FILE"

cp -R "$WORK_SRC_DIR"/. "$CLASSES_DIR"/
find "$CLASSES_DIR" -name '*.java' -delete
mkdir -p "$CLASSES_DIR/map"
for map_id in $(seq 0 159); do
    if [[ ! -f "$CLASSES_DIR/map/$map_id" ]]; then
        : >"$CLASSES_DIR/map/$map_id"
    fi
done
if [[ -f "$ACCOUNT_CSV" ]]; then
    cp -- "$ACCOUNT_CSV" "$CLASSES_DIR/account.csv"
fi
if [[ -f "$REPO_DIR/delllllllllll.txt" ]]; then
    cp -- "$REPO_DIR/delllllllllll.txt" "$CLASSES_DIR/delllllllllll.txt"
fi

if [[ ! -f "$CLASSES_DIR/HeadlessMain.class" || ! -f "$CLASSES_DIR/TaThuAccountManager.class" ]]; then
    echo "Build lỗi: thiếu class runtime Tà Thú" >&2
    exit 1
fi

if [[ -d "$BUILD_ROOT" ]]; then
    mv -- "$BUILD_ROOT" "$BACKUP_DIR"
fi
if ! mv -- "$STAGING_DIR" "$BUILD_ROOT"; then
    if [[ -d "$BACKUP_DIR" ]]; then
        mv -- "$BACKUP_DIR" "$BUILD_ROOT"
    fi
    exit 1
fi
STAGING_DIR=''
rm -rf -- "$BACKUP_DIR"
trap - EXIT
echo "Ta Thu runtime built at $BUILD_ROOT/classes"
