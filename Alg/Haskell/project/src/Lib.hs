module Lib
    ( someFunc
    ) where

type Stack = [Int]
pop :: Stack -> (Int, Stack)
pop (x:xs) = (x, xs)
push :: Int -> Stack -> ((), Stack)
push v s = ((), v:s)


someFunc :: IO ()
someFunc = putStrLn "someFunc"
