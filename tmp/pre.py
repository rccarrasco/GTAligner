from os import listdir
from os.path import isfile, join

T = []
n = 0
for line in file('datos.txt'):
    if n % 2==0:
        T.append(line);
    n += 1

n = 0
lista = listdir('../target/test-classes/samples')
for name in sorted(lista):
    print name
    oname = name.replace('jpeg','txt')
    output = open(oname, 'w')
    output.write(T[n]);
    n += 1
    output.close()


