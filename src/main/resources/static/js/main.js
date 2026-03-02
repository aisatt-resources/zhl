// ===============================
// カート数量更新のAJAX処理
// ===============================
/**
 * カート内商品の数量を更新する
 * @param {number|string} cartItemId - 更新対象のカートアイテムID
 * @param {number} quantity - 新しい数量
 */
function updateQuantity(cartItemId, quantity) {
    if (quantity < 1) {
        alert('数量は1以上で入力してください');
        return;
    }
    // サーバーにPOSTリクエストを送信して数量を更新
    fetch(`/aisato/cart/update-quantity/${cartItemId}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: new URLSearchParams({
            'quantity': quantity
        })
    })
        .then(response => response.json())	// JSONとしてレスポンスを受け取る
        .then(data => {
            if (data.success) {
                // 成功時はメッセージ表示＆ページリロード
                alert(data.message);
                location.reload();
            } else {
                alert('エラー: ' + data.message);
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('更新に失敗しました');
        });
}

// ===============================
// カートアイテム削除のAJAX処理
// ===============================
/**
 * カート内の商品を削除する
 * @param {number|string} cartItemId - 削除対象のカートアイテムID
 */
function deleteCartItem(cartItemId) {
    if (!confirm('この商品をカートから削除しますか？')) {
        return;
    }
    // サーバーに削除リクエストを送信
    fetch(`/aisato/cart/delete-ajax/${cartItemId}`, {
        method: 'POST'
    })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                // 成功時はメッセージ表示＆ページリロード
                alert(data.message);
                location.reload();
            } else {
                alert('エラー: ' + data.message);
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('削除に失敗しました');
        });
}

// ===============================
// カートアイテムの色・サイズ更新のAJAX処理
// ===============================
/**
 * カート内商品のオプション（色・サイズ）を更新する
 * @param {number|string} cartItemId - 更新対象のカートアイテムID
 * @param {string} color - 選択した色
 * @param {string} size - 選択したサイズ
 */
function updateCartItemOptions(cartItemId, color, size) {
	// サーバーにPOSTリクエストを送信してオプションを更新
    fetch(`/aisato/cart/update/${cartItemId}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: new URLSearchParams({
            'color': color || '',
            'size': size || ''
        })
    })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
				// 成功時はメッセージ表示＆ページリロード
                alert(data.message);
                location.reload();
            } else {
                alert('エラー: ' + data.message);
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('更新に失敗しました');
        });
}