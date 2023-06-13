
#include <iostream>
#include <map>
#include <set>
#include <algorithm> // sortなど
using namespace std;
#include <cctype> // isUpperCase
#include <utility> // pair
#include <vector> // vector

bool isUpperCase(char c) {
    return std::isupper(c) != 0;
}

int main()
{
  string S;
  cin >> S;
  vector<string> words;
  for (int i = 0; i < S.size();)
  {
    int j = i+1; // 2文字以上のため自身は含めず探索していく
    while(j < S.size() && !isUpperCase(S[j])) {
      j++;
    }
    string word = S.substr(i, j + 1 - i);
    // 小文字変更
    word[0] = tolower(word[0]);
    word.back() = tolower(word.back());
    words.push_back(word);
    i = j + 1;
  }
  sort(words.begin(), words.end());
  string result = "";
  for (auto v : words)
  {
    // 大文字変更
    v[0] = toupper(v[0]);
    v.back() = toupper(v.back());
    result += v;
  }
  cout << result << endl;
}