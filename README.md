# EventGetter
EventGetter は DoorKeeper や Connpass で公開されている API を使用してイベント情報を取得するプログラムです。

## 使用言語・環境
* Java(1.8)
* Eclipse(MARS.2)

## Release Note
### 2017.06.16 Fri
* Connpass の処理では月の最終日の取得は不要だったので削除。

### 2017.06.14 Wed
* とりあえず GitHub に公開。
* URL を編集して HTTP/HTTPS 通信で JSON 形式の情報を取得する実装。
* 取得した JSON をそのまま System.out.println() でコンソール表示。
