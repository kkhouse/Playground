import matplotlib.pyplot as plt
import numpy as np
import math
import itertools
import random
from fractions import Fraction
import pandas as pd

prefix = "/Users/kk/Dev/Playground/Python/Math/Python-for-Liberal-Arts-Programmers-master/sample/9591/"

# def calc_distance(x1, y1, x2, y2):
#     return math.sqrt((x2-x1)**2 + (y2-y1)**2)

# def calc_tri_area(a,b,c):
#     s = (a+b+c)/2
#     return math.sqrt(s*(s-a)*(s-b)*(s-c))


# P = np.matrix([
#     [3,3,5,5,3],
#     [3,1,1,3,3]
#     ])

# th = np.radians(45)
# A = np.matrix([
#     [np.cos(th), np.sin(-th)],
#     [np.sin(th), np.cos(th)]
#     ])

# print(A*P)

# p = np.array(P)
# p2 = np.array(A*P)

# plt.plot(p[0,:], p[1,:])
# plt.plot(p2[0,:],p2[1,:])
# plt.axis('equal')
# plt.grid(color = '0.8')
# plt.show()

#順列
# num = {1,2,3,4,5}
# A = set(itertools.permutations(num, 3))
# print(len(A))
# for a in A:
#     print(a)

# print(math.factorial(5))

# #組み合わせ
# A = set(itertools.combinations(num, 3))
# print(len(A))
# for a in A:
#     print(a)

# cnt = 0
# for i in range(10000):
#     dice = random.randint(1,6) 
#     if dice == 1:
#         cnt += 1

# p = cnt / 10000
# print(p)

# x = Fraction(2,7) * Fraction(5,6) * Fraction(1,5)
# y = Fraction(2,7) * Fraction(5,6) * Fraction(1,5)
# print(x*y)

# score.csv の読み込み
# data = pd.read_csv(prefix + 'score.csv', encoding='SHIFT-JIS')
# print(data['数学'][0])

