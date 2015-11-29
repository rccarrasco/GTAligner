from PIL import Image
import sys

img = Image.open(sys.argv[1])
pix = img.load()
#print img.size
w = 0
s = 0
g = 0
p = 0
for x in range(img.size[0]):
    dark = False
    D = []
    for y in range(img.size[1]):
        val = pix[x, y]
        if val[0] + val[1] + val[2] < 300:
            w += 1
            dark = True
            D.append(y)
            if x + 1 == img.size[0]:
                p += 1
                img.s
            else:
                right = pix[x + 1, y]
                if right[0] + right[1] + right[2] > 300:
                    p += 1
                    pix[x,y] = 16581375

            
    if dark:
        s += 1
        g += max(D) - min(D)
#        print x, D, max(D) - min(D), g
        
        

print w, s, g, p
img.save("colored.tiff", "TIFF")
