<<<<<<< HEAD
## 🔹 自己紹介
- ITエンジニア（2.5年）
- 得意分野: Webアプリ開発（バックエンド:  Java）
- 業務経験: 保険系、銀行系の開発

---

## 🔹 技術スタック
### 言語
-  Java / JavaScript （学習中）/ SQL

### フレームワーク
-Spring Boot（学習中） / Spring

### DB・ミドルウェア
- SQL Server / Oracle / MySQL  

---

## 1. プロジェクト紹介

#aisattEC アイサットECサイト
1. プロジェクト概要
aisattEC は、Spring Boot と MyBatis フレームワークをベースに開発されたECショッピングサイトです。本プロジェクトは、ユーザー管理、商品展示、ショッピングカート、注文処理など、ECサイトの核心機能を網羅したオンラインショッピングプラットフォームの提供を目的としています。プロジェクトのコード構造は明確で、理解や拡張が容易であり、Spring Boot と MyBatis の統合開発を学ぶための参考プロジェクトとしても最適です。

## 2. 技術スタック
本プロジェクトでは主に以下の技術スタックを採用しています：
バックエンドフレームワーク: Spring Boot (バージョン 3.5.10)
永続層フレームワーク: MyBatis (バージョン 3.0.5)
テンプレートエンジン: Thymeleaf
データベース: MySQL
ビルドツール: Maven
開発ツール: Lombok (Java Bean の記述簡素化)、Spring Boot DevTools (ホットデプロイ)
テストフレームワーク: Spring Boot Starter Test、MyBatis Spring Boot Starter Test
Java バージョン: 17

## 3. プロジェクト構造
プロジェクトは典型的な Maven および Spring Boot の構造を採用しており、主なディレクトリとファイルの説明は以下の通りです：
<img width="993" height="607" alt="image" src="https://github.com/user-attachments/assets/b40f86a3-142a-4a4d-a046-a2a73b0f1246" />
<img width="996" height="616" alt="image" src="https://github.com/user-attachments/assets/57247bf9-20ca-4a09-b2b1-4d9e77541280" />
<img width="997" height="615" alt="image" src="https://github.com/user-attachments/assets/bae69eb8-ceec-4f43-9684-c5e8e01086f5" />
<img width="995" height="414" alt="image" src="https://github.com/user-attachments/assets/8e77226e-5aed-448b-b439-ab45f942b356" />




## 4. データベース設定
データベース関連の設定は src/main/resources/application.yml ファイルに記載されています：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/your_DB?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Tokyo
    username: root
    password: your pw
    driver-class-name: com.mysql.cj.jdbc.Driver
mybatis:
  mapper-locations: classpath:mapper/*.xml
  type-aliases-package: aisato.ec.entity
  configuration:
    map-underscore-to-camel-case: true
    cache-enabled: true
```

**ご注意ください**: 
* データベース名は aisato です。
* デフォルトのユーザー名は root、パスワードは xxxx です。本番環境では必ず強力なパスワードに変更してください。
* データベースのタイムゾーンは Asia/Tokyo に設定されています。
* データベース初期化スクリプト aisato_EC.sql には、データベースの作成、テーブル構造の定義、および初期データ（決済方法、商品カテゴリ、サンプル商品）の挿入を行うSQL文が含まれています。

## 5. 実行環境

*   **Java Development Kit (JDK)**: バージョン 17 以上
*   **Maven**: バージョン 3.6.0 以上
*   **MySQL**: バージョン 8.0 以上

## 6. 実行方法

以下の手順に従って本プロジェクトを実行してください：

### 6.1. データベース準備

1.  MySQL サーバーが起動していることを確認してください。
2.  MySQL クライアント（MySQL Workbench、DataGrip、またはコマンドラインなど）を使用して MySQL サーバーに接続してください。
3.  src/main/resources/aisato_EC.sql ファイル内の SQL 文を実行し、データベース aisato を作成し、テーブル構造とデータを初期化してください。

    ```sql
    -- データベース作成
    CREATE DATABASE IF NOT EXISTS aisato CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
    USE aisato;
    -- ... (aisato_EC.sql 内の他のテーブル作成およびデータ挿入文)
    ```

4.  application.yml の設定に従い、データベース接続情報（url、username、password）が正しいことを確認してください。

### 6.2. プロジェクトのビルドと実行

1.  **プロジェクトのクローンまたはダウンロード**:プロジェクトファイルをローカルディレクトリに解凍します。
2.  **プロジェクトルートディレクトリに移動**: コマンドラインまたはターミナルを開き、aisattECv1 ディレクトリに移動します。
3.  **プロジェクトのビルド**:  Maven を使用してプロジェクトをビルドします。
    ```bash
    mvn clean install
    ```
4.  **プロジェクトの実行**: 
    *   **Maven で実行**: 
        ```bash
        mvn spring-boot:run
        ```
    *   **IDE で実行**: IntelliJ IDEA や Eclipse などの IDE でプロジェクトをインポートし、aisato.ec.AisattEcApplication クラスを実行します。

プロジェクトが正常に起動した後、ブラウザで http://localhost:8080/home（または http://localhost:8080/。HomeController のルートパス / が home ビューにリダイレクトされるため）にアクセスすることで、ECサイトにアクセスできます。

## 7. 主要機能

### 7.1. ユーザー

*   **ユーザー登録**: 新規ユーザーは /register ページからアカウントを登録できます。
*   **ユーザーログイン**: 登録済みユーザーは /login ページからログインできます。
*   **個人情報**: ログイン済みユーザーは /user/profile ページで個人情報を閲覧・更新できます。
*   **住所管理**: ユーザーは配送先住所の追加、編集、削除、およびデフォルト住所の設定が可能です。
*   **ショッピングカート**: ログイン済み・未ログインの両方のユーザーがカート機能を利用可能。ログイン後は一時カートがユーザーアカウントに紐付けられます。

### 7.2. 商品

*   **商品展示**: ホームページ / ですべての商品を表示します。
*   **商品詳細**: 商品をクリックすると詳細情報を確認できます。
*   **商品カテゴリ**: 商品カテゴリ別に商品を閲覧できます（/category?categoryId=X）。
*   **商品検索**: キーワードによる商品検索が可能です（/search?keyword=XXX）。

### 7.3. 注文

*   **ショッピングカート**: 商品のカートへの追加、数量の変更が可能です。
*   **決済**: カートから決済フローに進みます。
*   **注文作成**: 注文を生成し、決済方法を選択します。
*   **注文履歴**: ユーザーはマイページで過去の注文履歴を確認できます。
*   **全部で９画面があります**

## 8. API インターフェース（一部例）

### 8.1. `HomeController`

*   `GET /`: ホームページ、すべての商品を表示
*   `GET /category`: カテゴリ ID に基づいて商品リストを取得
*   `GET /search`: キーワードに基づいて商品を検索

### 8.2. `UserController`

*   `GET /login`: ログインページを表示
*   `POST /login`: ユーザーログインリクエストを処理
*   `GET /register`: 登録ページを表示
*   `POST /register`: ユーザー登録リクエストを処理
*   `GET /logout`: ユーザーログアウト
*   `GET /user/profile`: ユーザーの個人情報と住所を表示
*   `POST /user/profile/update`: ユーザーの個人情報を更新
*   `GET /user/address/add`: 住所追加ページを表示
*   `GET /user/address/edit/{addressId}`: 住所編集ページを表示
*   `POST /user/address/save`: ユーザー住所を保存（新規追加または更新）
*   `GET /user/address/delete/{addressId}`: ユーザー住所を削除
*   `POST /user/address/set-default/{addressId}`: デフォルト住所を設定

## 9. 注意事項

*   **ポート**: プロジェクトはデフォルトで 8080 ポートで実行されます。
*   **コンテキストパス**: プロジェクトのコンテキストパスは /home であるため、ホームページへのアクセスは http://localhost:8080/home となります。
*   **静的リソース**: 静的リソース（CSS、JS、画像）は src/main/resources/static ディレクトリに配置されています。
*   **テンプレートファイル**: Thymeleaf テンプレートファイルは src/main/resources/templates ディレクトリに配置されています。
*   **商品画像**：本システムではFREEPIKの無料画像素材を利用しております。
商用利用・販売目的ではなく、学習および研修を目的とした開発物です。
肖像権や著作権等の権利を侵害する意図は一切ございません。
--- 
### プロジェクトレイアウト展示

*   **ホームページ**:ユーザー未ログイン状態 http://localhost:8080/home/
*   **ポイント**：商品名検索、ユーザー登録、ログイン、カート画面遷移、商品詳細画面遷移、商品種別表示、アイサットLOGOクリックで更新＆ホームページ戻す
*   **スクリーンショット**
<img width="1920" height="967" alt="image" src="https://github.com/user-attachments/assets/33b0c1a6-0dc5-4dfc-9890-136025993534" />


*   **商品詳細画面**:ユーザー未ログイン状態 http://localhost:8080/home/product/1
*   **ポイント**：ユーザー登録、ログイン、カートボタン、商品個数ボタン、商品色とサイズボタン、アイサットLOGOクリックで更新＆ホームページ戻す
*   **スクリーンショット**
<img width="1920" height="976" alt="image" src="https://github.com/user-attachments/assets/50851f5c-3211-474a-b9cc-8b1831cedbe5" />


*   **カート画面**:ユーザー未ログイン状態 http://localhost:8080/home/cart
*   **ポイント**：ユーザー登録、ログイン、カートボタン、商品個数ボタン、商品削除ボタン、レジ画面遷移ボタン、買い物遷移ボタン、アイサットLOGOクリックで更新＆ホームページ戻す、レジ画面遷移はログインする必要がある
*   **スクリーンショット**
<img width="1920" height="971" alt="image" src="https://github.com/user-attachments/assets/26008493-f598-47a9-9156-289c47a70eb2" />


*   **ユーザーログイン画面**:ユーザー未ログイン状態 http://localhost:8080/home/login
*   **ポイント**：ユーザー登録ボタン、ログインボタン、アイサットLOGOクリックで更新＆ホームページ戻す
*   **スクリーンショット**
<img width="1920" height="974" alt="image" src="https://github.com/user-attachments/assets/04e8cc1b-d382-4f9a-892f-c3cb026da2be" />


*   **ユーザー登録画面**:ユーザー未ログイン状態 http://localhost:8080/home/register
*   **ポイント**：ユーザー登録ボタン、ログインボタン、アイサットLOGOクリックで更新＆ホームページ戻す
*   **スクリーンショット**
<img width="1920" height="976" alt="image" src="https://github.com/user-attachments/assets/a6e27c60-e8cb-4bed-8128-5c03067f7f26" />


*   **レジ画面**:ユーザーログイン状態 http://localhost:8080/home/order/checkout
*   **ポイント**：メニューボタン、新し住所追加ボタン、決済カート選択、注文ボタン、アイサットLOGOクリックで更新＆ホームページ戻す,注文する場合は必ず住所追加する必要があります。
*   **スクリーンショット**
<img width="1920" height="867" alt="image" src="https://github.com/user-attachments/assets/b2d8fc0a-a12b-4055-b6e6-15061c830da5" />
<img width="1920" height="786" alt="image" src="https://github.com/user-attachments/assets/ee7edeb6-308a-43bf-b2e0-d37446607af8" />


*   **住所登録画面**:ユーザーログイン状態 http://localhost:8080/home/user/address/add
*   **ポイント**：メニューボタン、登録ボタン、キャンセルボタン、アイサットLOGOクリックで更新＆ホームページ戻す
*   **スクリーンショット**
<img width="1920" height="970" alt="image" src="https://github.com/user-attachments/assets/2dc68188-7390-4692-a43b-310af4c5e38b" />
<img width="1920" height="623" alt="image" src="https://github.com/user-attachments/assets/b3351ae3-17ee-4a56-8187-fd2056977237" />


*   **商品注文完了画面**:ユーザーログイン状態 http://localhost:8080/home/order/success
*   **ポイント**：メニューボタン、トップページ戻るボタン、注文履歴ボタン、アイサットLOGOクリックで更新＆ホームページ戻す
*   **スクリーンショット**
<img width="1920" height="969" alt="image" src="https://github.com/user-attachments/assets/58f654c5-d00a-498a-a82f-02ecfedd69e6" />
<img width="1920" height="417" alt="image" src="https://github.com/user-attachments/assets/8aca034d-bb77-4749-aef1-433f4b826dfe" />


*   **注文履歴画面**:ユーザーログイン状態 http://localhost:8080/home/order/history
*   **ポイント**：メニューボタン、アイサットLOGOクリックで更新＆ホームページ戻す
*   **スクリーンショット**
<img width="1920" height="922" alt="image" src="https://github.com/user-attachments/assets/8dc09c6f-6648-4f88-8fc1-6b7aaee78b18" />
<img width="1920" height="787" alt="image" src="https://github.com/user-attachments/assets/b700b7f4-9c0a-4ff4-a650-19006079908f" />


*   **個人情報画面**:ユーザーログイン状態 http://localhost:8080/home/user/profile
*   **ポイント**：メニューボタン、個人情報ボタン、住所追加＆削除＆デフォルト設定＆編集ボタン、注文履歴＆ショッピングカート画面遷移、アイサットLOGOクリックで更新＆ホームページ戻す
*   **スクリーンショット**
<img width="1920" height="883" alt="image" src="https://github.com/user-attachments/assets/0ff0c304-572c-4dcb-a71b-1678d2f93bb7" />
<img width="1920" height="815" alt="image" src="https://github.com/user-attachments/assets/5a3984f9-0a84-43df-804f-901c995685ef" />
<img width="1920" height="427" alt="image" src="https://github.com/user-attachments/assets/338d20a4-8e72-4aba-af5f-78171340c57d" />


*   **メニュー**:ユーザーログイン状態 http://localhost:8080/home/user/profile
*   **ポイント**：注文履歴、マイベージ、ログアウト
*   **スクリーンショット**
<img width="1920" height="317" alt="image" src="https://github.com/user-attachments/assets/bbbde84e-675d-48ee-8ee6-e07dbae393c7" />



















