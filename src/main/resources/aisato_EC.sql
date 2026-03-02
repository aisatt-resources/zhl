-- 创建データベース
CREATE DATABASE IF NOT EXISTS aisato CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE aisato;

-- ユーザーテーブル
CREATE TABLE users (
    user_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ユーザーID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT 'ユーザー名',
    password VARCHAR(255) NOT NULL COMMENT 'パスワード(暗号化保存)',
    email VARCHAR(100) NOT NULL UNIQUE COMMENT 'メールアドレス',
    phone VARCHAR(20) COMMENT '電話番号',
    real_name VARCHAR(50) COMMENT '本名',
    gender TINYINT COMMENT '性別(0:女性,1:男性)',
    birth_date DATE COMMENT '生年月日',
    status TINYINT DEFAULT 1 COMMENT 'ステータス(0:無効,1:有効)',
    create_Time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '作成日時',
    update_Time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新日時'
) COMMENT='ユーザーテーブル';

-- ユーザー住所テーブル
CREATE TABLE user_addresses (
    address_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '住所ID',
    user_id BIGINT NOT NULL COMMENT 'ユーザーID',
    receiver_name VARCHAR(50) NOT NULL COMMENT '受取人氏名',
    phone VARCHAR(20) NOT NULL COMMENT '連絡先電話番号',
    province VARCHAR(50) NOT NULL COMMENT '都道府県',
    city VARCHAR(50) NOT NULL COMMENT '市区町村',
    district VARCHAR(50) NOT NULL COMMENT '区/郡',
    detail_address VARCHAR(200) NOT NULL COMMENT '詳細住所',
    postal_code VARCHAR(10) COMMENT '郵便番号',
    is_default TINYINT DEFAULT 0 COMMENT 'デフォルト住所(0:いいえ,1:はい)',
    create_Time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '作成日時',
    update_Time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新日時',
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
) COMMENT='ユーザー住所テーブル';

-- 支払い方法テーブル
CREATE TABLE payment_methods (
    method_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '支払い方法ID',
    method_name VARCHAR(50) NOT NULL COMMENT '支払い方法名称',
    method_code VARCHAR(20) NOT NULL UNIQUE COMMENT '支払い方法コード(例:credit_card,paypay,rakuten_payなど)',
    description VARCHAR(200) COMMENT '説明',
    is_active TINYINT DEFAULT 1 COMMENT 'アクティブ状態(0:いいえ,1:はい)',
    sort_order INT DEFAULT 0 COMMENT '並び順',
    create_Time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '作成日時'
) COMMENT='支払い方法テーブル';

-- ユーザー支払い設定テーブル
CREATE TABLE user_payment_settings (
    setting_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '設定ID',
    user_id BIGINT NOT NULL COMMENT 'ユーザーID',
    default_method_id BIGINT NOT NULL COMMENT 'デフォルト支払い方法ID',
    create_Time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '作成日時',
    update_Time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新日時',
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (default_method_id) REFERENCES payment_methods(method_id)
) COMMENT='ユーザー支払い設定テーブル';

-- 商品カテゴリーテーブル
CREATE TABLE categories (
    category_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'カテゴリーID',
    category_name VARCHAR(100) NOT NULL COMMENT 'カテゴリー名称',
    parent_id BIGINT DEFAULT 0 COMMENT '親カテゴリーID(0はトップレベル)',
    level TINYINT DEFAULT 1 COMMENT '階層レベル',
    sort_order INT DEFAULT 0 COMMENT '並び順',
    status TINYINT DEFAULT 1 COMMENT 'ステータス(0:非表示,1:表示)',
    create_Time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '作成日時',
    update_Time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新日時'
) COMMENT='商品カテゴリーテーブル';

-- 商品マスターテーブル
CREATE TABLE products (
    product_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '商品ID',
    product_name VARCHAR(200) NOT NULL COMMENT '商品名称',
    product_code VARCHAR(50) NOT NULL UNIQUE COMMENT '商品コード',
    category_id BIGINT NOT NULL COMMENT 'カテゴリーID',
    brand VARCHAR(100) COMMENT 'ブランド',
    price DECIMAL(10,2) NOT NULL COMMENT '価格',
    stock_quantity INT NOT NULL DEFAULT 0 COMMENT '在庫数量',
    description TEXT COMMENT '商品説明',
    image_url VARCHAR(500) COMMENT '画像URL',
    sales_status TINYINT DEFAULT 1 COMMENT '販売ステータス(0:非販売,1:販売中)',
    weight DECIMAL(8,2) COMMENT '重さ(g)',
    dimensions VARCHAR(50) COMMENT '寸法(長さ×幅×高さ)',
    color_options JSON COMMENT '色オプション(JSON形式)',
    size_options JSON COMMENT 'サイズオプション(JSON形式)',
    create_Time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '作成日時',
    update_Time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新日時',
    FOREIGN KEY (category_id) REFERENCES categories(category_id)
) COMMENT='商品マスターテーブル';

-- カートアイテムテーブル
CREATE TABLE cart_items (
    cart_item_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'カートアイテムID',
    user_id BIGINT NOT NULL COMMENT 'ユーザーID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    quantity INT NOT NULL DEFAULT 1 COMMENT '数量',
    selected_color VARCHAR(50) COMMENT '選択された色',
    selected_size VARCHAR(50) COMMENT '選択されたサイズ',
    add_Time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '追加日時',
    update_Time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新日時',
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (product_id) REFERENCES products(product_id)
) COMMENT='カートアイテムテーブル';

-- 注文マスターテーブル
CREATE TABLE orders (
    order_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '注文ID',
    order_no VARCHAR(50) NOT NULL UNIQUE COMMENT '注文番号',
    user_id BIGINT NOT NULL COMMENT 'ユーザーID',
    order_status TINYINT NOT NULL DEFAULT 10 COMMENT '注文ステータス(10:注文中,20:支払確認,30:発送準備,40:発送済み,50:完了,60:キャンセル)',
    total_amount DECIMAL(12,2) NOT NULL COMMENT '合計金額',
    shipping_fee DECIMAL(10,2) DEFAULT 0.00 COMMENT '送料',
    receiver_name VARCHAR(50) NOT NULL COMMENT '受取人氏名',
    receiver_phone VARCHAR(20) NOT NULL COMMENT '受取人電話番号',
    receiver_province VARCHAR(50) NOT NULL COMMENT '受取人都道府県',
    receiver_city VARCHAR(50) NOT NULL COMMENT '受取人市区町村',
    receiver_district VARCHAR(50) NOT NULL COMMENT '受取人区/郡',
    receiver_detail_address VARCHAR(200) NOT NULL COMMENT '受取人詳細住所',
    receiver_postal_code VARCHAR(10) NOT NULL COMMENT '受取人郵便番号',
    payment_method_id BIGINT NOT NULL COMMENT '支払い方法ID',
    payment_status TINYINT DEFAULT 0 COMMENT '支払いステータス(0:未支払い,1:支払い済み,2:支払い失敗,3:返金)',
    payment_transaction_id VARCHAR(100) COMMENT '支払いトランザクションID',
    remarks TEXT COMMENT '備考',
    create_Time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '作成日時',
    update_Time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新日時',
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (payment_method_id) REFERENCES payment_methods(method_id)
) COMMENT='注文マスターテーブル';

-- 注文件テーブル
CREATE TABLE order_items (
    order_item_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '注文件ID',
    order_id BIGINT NOT NULL COMMENT '注文ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    product_name VARCHAR(200) NOT NULL COMMENT '商品名称',
    product_price DECIMAL(10,2) NOT NULL COMMENT '商品価格',
    quantity INT NOT NULL COMMENT '数量',
    selected_color VARCHAR(50) COMMENT '選択された色',
    selected_size VARCHAR(50) COMMENT '選択されたサイズ',
    subtotal_amount DECIMAL(12,2) NOT NULL COMMENT '小計金額',
    create_Time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '作成日時',
    FOREIGN KEY (order_id) REFERENCES orders(order_id),
    FOREIGN KEY (product_id) REFERENCES products(product_id)
) COMMENT='注文件テーブル';



-- 初期データ挿入

-- 支払い方法初期データ
INSERT INTO payment_methods (method_name, method_code, description, sort_order) VALUES
('クレジットカード', 'credit_card', '各種クレジットカード', 1),
('PayPay', 'paypay', 'PayPay決済', 2),
('楽天ペイ', 'rakuten_pay', '楽天ペイ決済', 3),
('銀行振込', 'bank_transfer', '銀行振込', 4);

-- サンプル商品カテゴリ
INSERT INTO categories (category_name, parent_id, level, sort_order) VALUES
('ファッション', 0, 1, 1),
('電子機器', 0, 1, 2),
('ホーム&キッチン', 0, 1, 3),
('スポーツ&アウトドア', 0, 1, 4),
('本・CD・DVD', 0, 1, 5),
('調理器具', 3, 2, 1);

-- サンプル商品（6件、画像URL追加）
INSERT INTO `products` (`product_name`, `product_code`, `category_id`, `brand`, `author`, `price`, `stock_quantity`, `description`, `image_url`, `sales_status`, `weight`, `dimensions`, `color_options`, `size_options`, `create_Time`, `update_Time`) 
VALUES 
('ファイヤーバード トラックトップ', 'TSHIRT001', 1, 'Adidas', NULL, 13200, 10000, 'ゆったりとしたフィット感のトラックトップで、クラシックなスリーストライプススタイルを見せつけよう。\r\nアディダスの伝統を語る上で欠かせないものの1つが、このファイヤーバード トラックトップ（ジャージ）。スタンドカラーからリブ編みの裾まで、一目でわかるシルエット。\r\n\r\nアディカラー・スポーツ・シリーズの一部であり、大胆なエネルギーを感じられる一着。トリコット生地は、いつでも滑らかで快適な肌触り。また、ルーズフィットのデザインなので、ゆったりとした着心地でリラックスできる。\r\n\r\nジーンズやセットのトラックパンツとの相性も抜群で、さまざまな場面にマッチ。さらに、目を引くトレフォイルとスリーストライプスで、アディダススタイルを常にアピール。', '/images/tshirt.jpg', 1, NULL, NULL, '[\"ホワイト\", \"ブラック\", \"ブルー\"]', '[\"S\", \"M\", \"L\", \"XL\"]', '2026-02-13 12:34:53', '2026-02-26 13:54:27');
('iphone17pro Max', 'PHONE001', 2, '‎Apple', NULL, 229800, 50000, 'Apple iPhone 17 Pro Max (512 GB)：ProMotion を採用した6.9 インチディスプレイ、 A19 Pro チップ、iPhone 史上最長のバッテリー駆動時間、センターフレームフロント カメラを搭載したPro Fusion カメラシステム；ディープブルー', '/images/smartphone.jpg', 1, 204, '‎0.87 x 7.8 x 16.34 cm', '[\"ディープブルー\", \"コズミックオレンジ\", \"シルバー\"]', '[\"128GB\", \"256GB\", \"512GB\"]', '2026-02-13 12:34:53', '2026-02-20 13:02:49');
(' MacBook Pro 10', 'LAPTOP001', 2, '‎Apple', NULL, 238747, 1000, 'Apple 2025 MacBook Pro 10 コアCPU、10 コアGPU のM5 チップ搭載ノートパソコン：Apple Intelligence のために設計、14.2 インチLiquid Retina XDR ディスプレイ、16GB ユニファイドメモリ、512GBのSSD ストレージ - スペースブラック', '/images/laptop.jpg', 1, 1550, '31.26 x 22.12 x 1.55cm', '[\"シルバー\", \"ブラック\"]', '[\"13インチ\", \"15インチ\"]', '2026-02-13 12:34:53', '2026-02-20 13:13:38');
('ウォックパン', 'KITCHEN001', 3, 'ニトリ', NULL, 4490, 1000, '●使いはじめのこびりつきにくさが長持ち\r\n●ベストなタイミングで調理スタートできるお知らせマーク\r\n●優れたチタンコーティングでこびりつきにくい\r\n●均一な熱伝導の底面\r\n●PFOA・鉛・カドミウム不使用', '/images/kitchen_set.jpg', 1, 790, '\r\n\r\n47.1×29.5×9.3cm', '[\"ブラック\"]', '[\"20\", \"24\", \"26\", \"28\"]', '2026-02-13 12:34:53', '2026-02-20 13:13:43');
('ビジネスリュック', 'BACKPACK001', 4, 'Hp hope', NULL, 5900, 10000, '[Hp hope] ビジネスリュック 防水 大容量 軽量 男女兼用 PC バッグ USB充電ポート付き ファッションリュック 耐衝撃 通勤 通学 旅行 サイクリングに最適 15.6インチ', '/images/backpack.jpg', 1, 800, '42×30×18cm', '[\"ブラック\", \"グリーン\", \"ブルー\"]', '[\"S\", \"M\", \"L\"]', '2026-02-13 12:34:53', '2026-02-20 13:13:47');
('死ぬまでに観に行きたい世界の超絶美術を1冊でめぐる旅', 'BOOK001', 5, NULL, '山上　やすお', 2200, 10000, '海外旅行添乗員/アート系ユーチューバー\r\n博物館学芸員資格保有\r\n兵庫県伊丹市出身。幼少の頃から絵を描くことが好きで、大学では美術部で美術を学び、イラストの制作も開始。2007年株式会社フォーラムジャパン入社。海外をメインとした旅行の添乗員として1年の半分以上は世界各地を飛び回り、中でも美術館などアート関連での添乗に高評価を得る。2016年より、アートの素晴らしさをわかりやすく伝えるための講座を全国各地で開催。その後、コロナ禍に突入したことから、アートに関するYouTube「こやぎ先生の美術チャンネル」を開設。有名な画家や美術展などを構成・解説、親しみやすいイラストが人気を呼び、チャンネル登録者が2万人を超える。2021年に開始したオンライン美術講座は総受講者数が5千人を超え、日本最大級の習いごと検索サービス「ストアカ」にて新人先生賞とストアカンオブザイヤーをダブルで受賞。2022年10月よりNHK文化センター講師、およびNHK学園オンライン講座を担当。毎日放送『三度の飯よりアレが好き!』にて地上波デビュー。話し方伝え方スペシャリスト資格保有。著書に『死ぬまでに観に行きたい世界の有名美術を1冊でめぐる旅』（ダイヤモンド社）『マンガで「なるほど名画」こやぎ先生が教える西洋絵画の7つのポイント』（SBクリエイティブ）がある。', '/images/book.jpg', 1, 510, '21 x 14.8 x 2 cm', '[\"なし\"]', '[\"通常\"]', '2026-02-13 12:34:53', '2026-02-20 13:13:53');

