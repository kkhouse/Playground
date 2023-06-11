
import Data.List 

f :: Int -> Int -> Int -> Int
f n a b = sum.filter (\i -> i <= b && i >= a)  $ map digitSum [1..n]
  where
    digitSum 0 = 0
    digitSum i = i `mod` 10 + digitSum (i `div` 10)

shitOnly :: Int -> [Int] -> Int
shitOnly _ xs = minimum $ foldl f [] xs
  where f acc x = acc ++ [cDiv2 x]
        cDiv2 n 
          | odd n = 0
          | otherwise = 1 + cDiv2 (n `div` 2)
-- こっちの方がよい
shitOnly2 :: [Int] -> Maybe Int
shitOnly2 [] = Nothing
shitOnly2 xs = Just . minimum . map cDiv2 $ xs
  where cDiv2 n
          | odd n = 0
          | otherwise = 1 + cDiv2 (n `div` 2)

aliceVs :: [Int] -> Int
aliceVs xs = let sortedXs = reverse.sort $ xs
              in alice sortedXs `minus` bob sortedXs
              where 
                alice a = sum [x | (x, i) <- zip xs [0..], even i]
                bob b = sum [x | (x, i) <- zip xs [0..], odd i]

main :: IO ()
main = do
  putStrLn "input"