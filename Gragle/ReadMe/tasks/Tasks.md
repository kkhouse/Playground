# 初期設定での全タスクメモ
- Basicには Build Setup tasksとHelp tasksのみある
- Build Setup tasksとHelp tasksはどんなプロジェクトでも差異はない
- tasks とは taskグラフを表している。taskグラフは複数ある
- Build tasksは言語ごとに同じものが生成される。AndroidのみAndroid固有のTaskが追加されている
- BasicとAndroid以外でDocumentation tasksがある。Javadocコメントを生成してくれる
- BasicとAndroid以外でVerificationTasksがある。check or tasksというコマンドのみ
- 


全てに共通
- Build Setup tasks : ビルドのセットアップを行うためのタスク。
- Help tasks : : Gradleのタスク一覧を表示するためのタスク

Basicを除いて共通 （Androidは他とは違い独自のタスクが多め。）
- Verification tasks: テストの実行やビルド品質の検証など、ビルドの成果物の検証を行うためのタスク
- Build tasks : プロジェクトをビルドするためのタスク

BasicとAndroidを除いて共通
- Documentation tasks: プロジェクトのドキュメンテーションを生成するためのタスク

固有のもの
- Basic : なし
- application
        - Distribution tasks: ビルドアーティファクト（JAR、WAR、APK など）を生成して、配布するためのタスク
        - Application tasks: アプリケーションを実行するためのタスク
- gradle plugin
        - Plugin development tasks: ビルドや公開のためのタスク
- library
        - なし
- Android
        - Android tasks: Androidアプリのビルド、テスト、デプロイなどのタスクを提供する。
        - Install tasks: Androidアプリのインストールに関するタスクを提供する。

