module Main (main) where

import Introduction

-- [Char]あるいは String を Int にする
toInt :: (String -> Int)
toInt x = read x ::Int

toDouble :: (String -> Double)
toDouble x = read x ::Double

-- convert input to [Int]
getInts = do
  x <- getLine
  let y = (map$toInt)$words x
  return y

-- convert input to [Double]
getDoubles = do
  x <- getLine
  let y = (map$toDouble)$words x
  return y

-- test
-- shiftOnly = do
--   n <- readLn
--   ds <- replicateM n readLn
--   return ds

main = do
    (n:_) <- getInts
    (xs) <- getInts
    print $ shiftOnly n xs
