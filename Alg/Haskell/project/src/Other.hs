import Control.Monad
import Control.Monad.State
import Data.Array

import Data.List
import GHC.Exts.Heap (GenClosure(value))
import Data.Semigroup (diff)

log2 :: Int -> Int
log2 x = floor (logBase 2 (fromIntegral x))

gcn :: Int -> Int -> Int
gcn m n 
    | m `mod` n == 0 = n
    | otherwise = gcn n $ m `mod` n

-- fibo :: Int -> Int
-- fibo n 
--  | n == 0 = 0
--  | n == 1 = 1
--  | otherwise = fibo n-1 + fibo n-2

fibo :: Int -> Integer
fibo n = fibos ! n
    where fibos = listArray (0,n) [f i| i <- [0..n]]
          f 0 = 0
          f 1 = 1
          f i = fibos ! (i-1) + fibos ! (i-2)

-- listから最初のi個をとって総和をwにできるか
func :: [Int] -> Int -> Int -> Bool
func (x:xs) i w
    | i == 0 = w == 0
    | otherwise = func xs (i-1) w || func xs (i-1) (w-x) 

trib :: Int -> Int
trib a
    | a == 0 = 0
    | a == 1 = 0 
    | a == 2 = 1
    | otherwise = trib (a-1)+ trib (a-2) + trib (a-3)

tribMemo :: Int -> Int
tribMemo a = tribs ! a
    where tribs = listArray (0, a) [f i | i <- [0..a]]
          f 0 = 0
          f 1 = 0
          f 2 = 1
          f i = tribs ! (i-1) + tribs ! (i-2) + tribs ! (i-3) 

-- 数字のリストを整数に変換します。
listToNum :: [Int] -> Int
listToNum = foldl (\acc x -> acc * 10 + x) 0
-- 引数が753数かどうかを確認します。
is753 :: Int -> Bool
is753 num = all (`elem` show num) ['7', '5', '3']
-- 全ての753数を生成
gen753 :: Int -> [Int]
gen753 n = [x | x <- map listToNum $ replicateM n [7,5,3], is753 x]
-- k 以下の753数の数を返す
count753 :: Int -> Int
count753 k = length $ takeWhile (<= k) all753
    where all753 = concatMap gen753 [1..]