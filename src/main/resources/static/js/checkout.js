// DOMが完全に読み込まれた後に実行
document.addEventListener('DOMContentLoaded', function() {
    // フォームやボタンに必要なデータを取得
    const appData = document.getElementById('app-data');
    const successUrl = appData.dataset.successUrl;	// 注文成功後のリダイレクト先（未使用ですが保持）

    // ===============================
    // 住所カードクリックイベント
    // ===============================
    document.querySelectorAll('.address-card').forEach(function(card) {
        card.addEventListener('click', function() {
            // すべての住所カードの選択状態を解除
            document.querySelectorAll('.address-card').forEach(function(c) {
                c.classList.remove('selected');
            });
            // クリックしたカードを選択状態に
            this.classList.add('selected');
            // hidden input に選択された住所IDを設定
            document.getElementById('selectedAddressId').value = this.dataset.id;
        });
    });

    // ===============================
    // 支払い方法クリックイベント
    // ===============================
    document.querySelectorAll('.payment-method').forEach(function(method) {
        method.addEventListener('click', function() {
            document.querySelectorAll('.payment-method').forEach(function(m) {
                m.classList.remove('selected');
            });
            this.classList.add('selected');
            document.getElementById('selectedPaymentMethodId').value = this.dataset.id;
        });
    });

    // ===============================
    // 注文確定ボタンクリックイベント
    // ===============================
    const placeOrderBtn = document.getElementById('placeOrderBtn');
    if (placeOrderBtn) {
        placeOrderBtn.addEventListener('click', function(e) {
            e.preventDefault(); // デフォルトのフォーム送信をキャンセル

            // 選択された住所IDと支払い方法IDを取得
            const addressId = document.getElementById('selectedAddressId').value;
            const paymentMethodId = document.getElementById('selectedPaymentMethodId').value;
            // const remarks = document.getElementById('remarks').value;

            // 入力チェック
            if (!addressId) {
                alert('お届け先住所を選択してください');
                return;
            }

            if (!paymentMethodId) {
                alert('支払い方法を選択してください');
                return;
            }
            // 確認ダイアログ
            if (confirm('注文を確定しますか？')) {
                // ===============================
                // 1. 選択された住所情報をフォームに設定
                // ===============================
                const selectedAddress = document.querySelector('.address-card.selected');
                if (selectedAddress) {
                    const addressTexts = selectedAddress.querySelectorAll('.address-body p');
                    if (addressTexts.length >= 4) {
                        // 名前・電話番号・郵便番号をフォームに設定
                        document.getElementById('formReceiverName').value = addressTexts[0].textContent.trim();
                        document.getElementById('formReceiverPhone').value = addressTexts[1].textContent.trim();
                        document.getElementById('formReceiverPostalCode').value = addressTexts[2].textContent.trim();

                        // 住所詳細を設定（ここでは簡易版でフル住所をそのまま入力）
                        const fullAddress = addressTexts[3].textContent.trim();
                        document.getElementById('formReceiverDetailAddress').value = fullAddress;

                        // 都道府県・市区町村・区（仮の固定値、実運用ではDBから取得推奨）
                        document.getElementById('formReceiverProvince').value = '東京都'; // 仮
                        document.getElementById('formReceiverCity').value = '渋谷区';   // 仮
                        document.getElementById('formReceiverDistrict').value = '渋谷'; // 仮
                    }
                }

                // ===============================
                // 2. 支払い方法をフォームに設定
                // ===============================
                document.getElementById('formPaymentMethodId').value = paymentMethodId;

				// ===============================
				// 3. 備考をフォームに設定（必要ならコメント解除）
				// ===============================
                // document.getElementById('formRemarks').value = remarks;

				// ===============================
				// 4. フォーム送信
				// フォームの action="/order/confirm" にPOSTリクエストを送信
				// ===============================
                document.getElementById('orderForm').submit();
            }
        });
    }
});