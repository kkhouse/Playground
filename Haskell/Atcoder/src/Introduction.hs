module Introduction where

-- odd even
oddEven :: Int -> Int -> String
oddEven x y
    | (x `mod` y) == 0 = "Odd"
    | otherwise = "Even"

-- Serval vs Monster
type Monster = Double 
type Serval = Double
vs :: Monster -> Serval -> Int
vs x y = ceiling $ x/y

-- We love golf
loveGolf :: Int -> Int -> Int -> Bool
loveGolf k a b = not.null $ filter (\x -> x `mod` k == 0) [a..b] 

-- Some Sums
someSums :: Int -> Int -> Int -> Int
someSums n a b = sum $ filter (\x -> sumDigit x `elem` [a..b]) [1..n]
sumDigit :: Int -> Int -- 各けたの和を返す関数
sumDigit x
    | x < 10 = x
    | otherwise = (x `mod` 10) + (sumDigit $ x `div` 10)

-- Shift Only
--- 力技
shiftOnly :: Int -> [Int] -> Int
shiftOnly _ list = snd $ getDiv2Count list 0
getDiv2Count :: [Int]-> Int -> ([Int], Int)
getDiv2Count list count
    | isDiv2 list = getDiv2Count (div2 list) (count+1)
    | otherwise = (list, count)
isDiv2 :: [Int] -> Bool
isDiv2 list = foldl (\acc x -> acc && (x `mod` 2 == 0)) True list 
div2 :: [Int] -> [Int]
div2 list = map (\x -> x `div` 2) list

---- 別解  上をめっちゃ短縮したやつ（というか上が冗長）
_div2 :: Int -> [Int] -> Int
_div2 n a = if dividable
    then _div2 (n+1) (map (`div` 2) a)
    else n
        where dividable = all even a
-----　別解2　リストの各値を何回2で割り切れるかのカウント数にマッピングしちゃうという発想
__div2 :: Int -> [Int]
__div2 a = if a `mod` 2 == 0 then 1 + (__div2 (a `div` 2)) else 0
ans :: [Int] -> Int
ans a = minimum $ map __div2 