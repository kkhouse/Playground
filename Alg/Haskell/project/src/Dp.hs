import Control.Monad
import Control.Monad.State
import Data.Array

import Data.List
import GHC.Exts.Heap (GenClosure(value))
import Data.Semigroup (diff)
-- Flog 問題
minCost :: [Int] -> Int
minCost hs = dp ! (n-1)
    where
        n = length hs
        dp = listArray (0, n-1) $ 0 : map f [1..n-1]
        f i = min (dp ! (i-1) + abs(hs !! i - (hs !! (i-1))))
                 (if i > 1 then dp ! (i-2) + abs(hs !! i - hs !!(i-2)) else maxBound)

-- ナップザック問題
maxValue :: [Int] -> [Int] -> Int -> Int  
maxValue weights values maxW = dp ! (n, maxW)
    where
        n = length weights
        weights' = listArray (1, n) weights
        values' = listArray (1,n) values
        dp = array ((0,0),(n, maxW)) [((i, w), f i w) | i <- [0..n], w <- [0..maxW]]
        f 0 _ = 0
        f i w
            | weights' ! i > maxW = dp ! (i-1, w)
            | otherwise = max (dp ! (i-1, w)) (dp ! (i-1, w + (weights' ! i) + (values' ! i)))

-- 編集距離
editDistance :: String -> String -> Int
editDistance xs ys = dp ! (m,n)
    where
        (m,n) = (length xs, length ys)
        x = listArray (1,m) xs
        y = listArray (1,n) ys
        dp = array bnds [((i,j),cell i j) |  (i,j) <- range bnds]
        bnds = ((0,0),(m,n))

        cell :: Int -> Int -> Int
        cell i 0 = i
        cell 0 j = j
        cell i j = minimum [dp!(i-1, j)+1, dp!(i,j-1)+1, dp!(i-1,j-1)+c]
            where c = if x!i == y!j then 0 else 1


-- 5-1 
maxHappy :: [Int] -> [Int] -> [Int] -> Int -> Int
maxHappy a b c n = maximum dp
    where
        dp = foldl f [0, 0, 0] $ zip3 a b c
            where f [pa,pb,pc] (a,b,c) = [max pb pc + a, max pa pc + b, max pa pb + c]

-- 5-2
subSet :: [Int] -> Int -> Bool
subSet xs target = dp ! (length xs , target)
    where
        bounds = ((0,0), (length xs, target))
        dp = listArray bounds [ entry ij | ij <- range bounds]
        entry (0,0) = True
        entry (0,_) = False
        entry (i,j)
            | xs !! (i-1) > j = dp ! (i-1, j)
            | otherwise = dp ! (i-1,j) || dp ! (i - 1, j - xs !! (i - 1)) 

-- 5-3
selection :: [Int] -> Int -> Int
selection xs w = 