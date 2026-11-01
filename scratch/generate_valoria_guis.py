import os
import zipfile
from PIL import Image

jar_path = os.path.expanduser('~/.gradle/caches/forge_gradle/minecraft_repo/versions/1.20.1/client-extra.jar')
output_dir = r"D:\KubeJS-GUI\src\main\resources\assets\komaru_recipe_exporter\textures\gui\valoria"
os.makedirs(output_dir, exist_ok=True)

with zipfile.ZipFile(jar_path) as z:
    with z.open('assets/minecraft/textures/gui/container/furnace.png') as f:
        furnace = Image.open(f).convert('RGBA')
    with z.open('assets/minecraft/textures/gui/container/crafting_table.png') as f:
        crafting_table = Image.open(f).convert('RGBA')

slot_sprite = crafting_table.crop((29, 16, 47, 34))        # 18x18
arrow_sprite = furnace.crop((79, 34, 103, 51))             # 24x17
result_box_sprite = furnace.crop((111, 30, 137, 56))       # 26x26

def make_base():
    base = crafting_table.copy()
    # Clear working area above player inventory (x=6..169, y=10..81) with standard #C6C6C6
    for y in range(10, 82):
        for x in range(6, 170):
            base.putpixel((x, y), (198, 198, 198, 255))
    return base

# 1. Heavy Anvil (Input: 48, 35; Arrow: 74, 34; Result: 120, 35)
anvil = make_base()
anvil.paste(slot_sprite, (47, 34), slot_sprite)
anvil.paste(arrow_sprite, (74, 34), arrow_sprite)
anvil.paste(result_box_sprite, (115, 30), result_box_sprite)
anvil.save(os.path.join(output_dir, "heavy_anvil.png"))

# 2. Manipulator (Input 1: 36, 44; Input 2: 72, 44; Arrow: 94, 44; Result: 128, 44)
manipulator = make_base()
manipulator.paste(slot_sprite, (35, 43), slot_sprite)
manipulator.paste(slot_sprite, (71, 43), slot_sprite)
manipulator.paste(arrow_sprite, (94, 44), arrow_sprite)
manipulator.paste(result_box_sprite, (123, 39), result_box_sprite)
manipulator.save(os.path.join(output_dir, "manipulator.png"))

# 3. Heavy Workbench (18 slots 6x3: items at (8 + col*18, 17 + row*18); Arrow: 116, 34; Result: 146, 35)
hw = make_base()
for row in range(3):
    for col in range(6):
        hw.paste(slot_sprite, (7 + col * 18, 16 + row * 18), slot_sprite)
hw.paste(arrow_sprite, (116, 34), arrow_sprite)
hw.paste(result_box_sprite, (141, 30), result_box_sprite)
hw.save(os.path.join(output_dir, "heavy_workbench.png"))

# 4. Alchemy (18 slots 6x3: items at (8 + col*18, 17 + row*18); Arrow: 116, 34; Result: 146, 35)
alchemy = make_base()
for row in range(3):
    for col in range(6):
        alchemy.paste(slot_sprite, (7 + col * 18, 16 + row * 18), slot_sprite)
alchemy.paste(arrow_sprite, (116, 34), arrow_sprite)
alchemy.paste(result_box_sprite, (141, 30), result_box_sprite)
alchemy.save(os.path.join(output_dir, "alchemy.png"))

# 5. Soul Infuser (Input 1: 36, 35; Input 2: 72, 35; Arrow: 94, 34; Result: 128, 35)
soul = make_base()
soul.paste(slot_sprite, (35, 34), slot_sprite)
soul.paste(slot_sprite, (71, 34), slot_sprite)
soul.paste(arrow_sprite, (94, 34), arrow_sprite)
soul.paste(result_box_sprite, (123, 30), result_box_sprite)
soul.save(os.path.join(output_dir, "soul_infuser.png"))

# 6. Jewelry (Input 1: 36, 35; Input 2: 72, 35; Arrow: 94, 34; Result: 128, 35)
jewelry = make_base()
jewelry.paste(slot_sprite, (35, 34), slot_sprite)
jewelry.paste(slot_sprite, (71, 34), slot_sprite)
jewelry.paste(arrow_sprite, (94, 34), arrow_sprite)
jewelry.paste(result_box_sprite, (123, 30), result_box_sprite)
jewelry.save(os.path.join(output_dir, "jewelry.png"))

# 7. Keg Brewery (4 inputs 2x2: items at (34, 26), (52, 26), (34, 44), (52, 44); Arrow: 80, 34; Result: 124, 35)
keg = make_base()
keg.paste(slot_sprite, (33, 25), slot_sprite)
keg.paste(slot_sprite, (51, 25), slot_sprite)
keg.paste(slot_sprite, (33, 43), slot_sprite)
keg.paste(slot_sprite, (51, 43), slot_sprite)
keg.paste(arrow_sprite, (80, 34), arrow_sprite)
keg.paste(result_box_sprite, (119, 30), result_box_sprite)
keg.save(os.path.join(output_dir, "keg_brewery.png"))

# 8. Crusher (Input: 48, 35; Arrow: 74, 34; Result: 120, 35)
crusher = make_base()
crusher.paste(slot_sprite, (47, 34), slot_sprite)
crusher.paste(arrow_sprite, (74, 34), arrow_sprite)
crusher.paste(result_box_sprite, (115, 30), result_box_sprite)
crusher.save(os.path.join(output_dir, "crusher.png"))

print("All 8 Valoria textures successfully generated from authentic vanilla assets!")
