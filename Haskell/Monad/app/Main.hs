module Main (main) where

import System.IO
import System.Directory
import System.Environment
import Data.List
import Control.Exception
import Control.Monad.Writer

-- IO Sample Start
dispatch :: String -> [String] -> IO ()
dispatch "add" = add
dispatch "view" = view
dispatch "remove" = remove

add :: [String] -> IO ()
add [fileName, todoItem] = appendFile fileName (todoItem ++ "\n")

view :: [String] -> IO ()
view [fileName] = do
    content <- readFile fileName
    let todoTasks = lines content
        numberedTasks = zipWith (\n line -> show n ++ " - " ++ line) [0..] todoTasks
    putStr $ unlines numberedTasks

remove :: [String] -> IO ()
remove [fileName, numberTodo] = do
    content <- readFile fileName
    let todoTasks = lines content
        numberedTasks = zipWith (\n line -> show n ++ " - " ++ line) [0..] todoTasks

    putStrLn "There are your TO-DO items:"
    mapM_ putStrLn numberedTasks
    let number = read numberTodo
        newTodoItems = unlines $ delete (todoTasks !! number) todoTasks

    bracketOnError (openTempFile "." "temp")
        (\(tempName, tempHandle) -> do
            hClose tempHandle
            removeFile tempName)
        (\(tempName, tempHandle) -> do
            hPutStr tempHandle newTodoItems
            hClose tempHandle
            removeFile fileName
            renameFile tempName fileName)

-- IO Sample end 


-- 関数型問題解決手法 Start

-- 逆ポーランド記法
-- resolve :: String -> Int
-- resolve expression = foldl foldingFunc [] (words expression)
    -- where   foldingFunc (x:y:ys) "+" = (x+y):ys
    --         foldingFunc (x:y:ys) "-" = (x-y):ys
    --         foldingFunc (x:y:ys) "*" = (x*y):ys
    --         foldingFunc xs number = read number:xs

-- 最短経路問題

-- ○最終的にどんな型になる関数かを考える
-- 道路網（RodeSystem）を引数にとり、経路（Path）を返したい
-- 道路網のデータ構造を考える
data Section = Section { getA :: Int, getB :: Int, getC :: Int } deriving (Show)
type RoadSystem = [Section] -- [Section(50,30,10), Section(5,90,20)...]
-- 経路のデータ構造を考える
data Label = A | B | C deriving (Show)
type Path = [(Label, Int)] -- {Ato50} {Bto10}

-- 最上位の関数を定義しておく
resolve :: RoadSystem -> Path -- (Bto10, Cto30, Ato5...)

{- 
A1への最短は A0+50 vs B0+30+10
B1への最短は A0+50+10 vs B0+30
A2への最短は A1+5 vs B1+90+20
B2への最短は A1+5+20 vs B1+90
一つ前の最短 + Section から次への最短を繰り返し求めていく動作が必要
(最短A,最短B）-> Section -> (次の最短A,次の最短B)
-}
loadStep :: (Path, Path) -> Section -> (Path, Path)
loadStep (pathA, pathB) (Section a b c) = 
    let timeA = sum (map snd pathA)
        timeB = sum (map snd pathB)
        forwardTimeToA = timeA + a
        crossTimeToA = timeB + b + c
        forwardTimeToB = timeB + b
        crossTimeToB = timeA + a + c
        newPathA =  if forwardTimeToA <= crossTimeToA
                    then (A,a):pathA
                    else (C,c):(B,b):pathB
        newPathB =  if forwardTimeToB <= crossTimeToB
                    then (B,b):pathB
                    else (C,c):(A,a):pathA
    in (newPathA, newPathB)

toLondon :: RoadSystem
toLondon = [Section 50 10 30, Section 5 90 20, Section 40 2 25, Section 10 8 0]

resolve road = 
    let (resolveA, resolveB) = foldl loadStep ([],[]) road
    in if sum (map snd resolveA) <= sum (map snd resolveB)
        then reverse resolveA
        else reverse resolveB


-- 関数型問題解決手法 End

-- Monad Start


logNumber :: Int -> Writer [String] Int
logNumber x = writer (x , ["Writing Number Is : " ++ show x])

multWithLog :: Writer [String] Int
multWithLog  = do
    a <- logNumber 2
    b <- logNumber 3
    return (a*b)

-- gcd' :: Int -> Int -> Int
-- gcd' a b
--     | b == 0 = a
--     | otherwise gcd' b (a 'mod' b)

-- gcd' :: Int -> Int -> Writer [String] Int
-- gcd' a b
--     | b == 0 = do
--         tell ["Finished With :" ++ show a]
--         return a
--     | otherwise = do
--         tell [(show a) ++ " mod " ++ (show b) ++ " = " ++ (show $ mod a b)]
--         gcd' b (mod a b)

-- knight
type KnightPos = (Int, Int)

moveKnight :: KnightPos -> [KnightPos]
moveKnight (c,r) = do
    (cc, rr) <- [
        (c+2, r-1),(c+2, r+1),(c-2, r+1),(c+2, r-1),
        (c+1, r-2),(c-1, r-2),(c+1, r+2),(c-1, r+2)]
    guard (cc `elem` [1..8] && rr `elem` [1..8])
    return (cc, rr)

in3 :: KnightPos -> [KnightPos]
in3 start = do
    first <- moveKnight start
    second <- moveKnight first
    moveKnight second

canReachIn3 :: KnightPos -> KnightPos -> Bool
canReachIn3 start end = end `elem` in3 start

-- knight monadic

-- in3 :: KnightPos -> [KnightPos]
-- in3 start = return start >>= moveKnight >>= moveKnight >>= moveKnight
-- この連続性は関数合成を使うとこう書ける。同じ関数を複数回適応したい時に使える。
-- <=<は(.)のモナド版 
inMany :: Int -> KnightPos -> [KnightPos]
inMany x start = return start >>= foldr (<=<) return (replicate x moveKnight)

canReachIn :: Int -> KnightPos -> KnightPos -> Bool
canReachIn x start end = end `elem` inMany x start

-- 逆ポーランド記法 monadic
-- String -> Intへの変換をMaybeで扱う
readMaybe :: (Read a) => String -> Maybe a
readMaybe st = case reads st of [(x, "")] -> Just x
                                _ -> Nothing
-- 畳み込み関数にもMaybeを扱う
foldingFunc :: [Double] -> String -> Maybe [Double]
foldingFunc (x:y:ys) "+" = Just ((x+y):ys)
foldingFunc (x:y:ys) "-" = Just ((x-y):ys)
foldingFunc (x:y:ys) "*" = Just ((x-y):ys)
foldingFunc xs number = liftM (:xs) (readMaybe number)

-- Maybe [Double] のリスト内が一つの結果になっているかを確認する
checkSize :: [Double] -> Maybe Double
checkSize [result] = return result
checkSize _ = Nothing

monadRPN :: String -> Maybe Double
monadRPN st = foldM foldingFunc [] (words st) >>= checkSize

-- monadRPN st = do
--     [result] <- foldM foldingFunc [] (words st)
--     return result
--- Zipper

data Tree a = Empty | Node a (Tree a) (Tree a) deriving (Show)

main = do 
    print $ monadRPN "1 2 * "




