import Data.List (sort)


binarySearch :: [Int] -> Int -> Bool
binarySearch [] _ = False
binarySearch xs key
  | mid == key = True
  | mid < key = binarySearch lower key
  | otherwise = binarySearch upper key
  where
      (lower, mid : upper) = splitAt (length xs `div` 2) xs

minValue :: Int -> Int -> [Int] -> [Int] -> Int
minValue n k a b = minimum $ map f a
    where
        sortedB = sort b
        f ai = let bi = head $ dropWhile (<k-ai) sortedB 
                in bi + ai