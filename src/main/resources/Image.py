from PIL import Image
img = Image.open('sample1.jpeg')
pix = img.load()
#print img.size
w = 0
s = 0
for x in range(img.size[0]):
    dark = False
    for y in range(img.size[1]):
        val = pix[x, y]
        # print val[0], val[1], val[2]
        if val[0] + val[1] + val[2] < 300:
            w += 1
            dark = True
    if dark:
        s += 1

print w, s
