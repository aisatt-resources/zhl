/**
 * カートページ用JavaScript
 * ログイン/未ログイン問わず安全に動作
 * ポップアップなしで数量更新・削除を行う
 */

/**
 * デバウンス関数
 * 指定時間内に連続呼び出しがあった場合、最後の1回のみ実行
 *
 * @param {Function} func - 実行する関数
 * @param {number} delay - 遅延時間（ms）
 * @returns {Function} デバウンスされた関数
 */
function debounce(func, delay) {
    let timeout;
    return function() {
        const context = this;
        const args = arguments;
        clearTimeout(timeout);
        timeout = setTimeout(() => func.apply(context, args), delay);
    };
}

/**
 * カートUIを更新する
 * 合計金額・数量・アイコン表示を更新
 *
 * @param {Object} data - サーバーから取得したカート情報
 * @param {number} data.cartTotal - カート合計金額
 * @param {number} data.cartCount - カート合計数量
 */
function refreshCartUI(data) {

    console.log("UIデータを更新する:", data);

    const totalEl = document.getElementById('cart-total');	//	合計金額を更新
    const countEl = document.getElementById('cart-count');	//	商品合計数量を更新
    const iconEl = document.getElementById('cart-icon-count');	//	カートアイコン数を更新
    const subtotalAmountEl = document.getElementById('subtotal-amount');	//	商品合計金額を更新

    //	商品合計を更新
    if (subtotalAmountEl && data.cartTotal !== undefined) {
        subtotalAmountEl.textContent = data.cartTotal;
    }

    //	合計金額を更新
    if (totalEl && data.cartTotal !== undefined) {
        totalEl.textContent = data.cartTotal;
    }

    //	商品合計数量を更新
    if (countEl && data.cartCount !== undefined) {
        countEl.textContent = data.cartCount;
    }

    //	カートアイコン数を更新
    if (iconEl && data.cartCount !== undefined) {
        iconEl.textContent = data.cartCount;
    }
}
document.addEventListener('DOMContentLoaded', function() {

    let isUpdating = false;

    const cartData = document.getElementById('cart-js-data');

    if (!cartData) {
        console.error('cart-js-data要素が見つかりません');
        return;
    }

    // ログイン判定・ユーザ情報
    const isLoggedinRaw = cartData.dataset.isLoggedin;
    const userId = cartData.dataset.userId || '';
    const isEmpty = cartData.dataset.isEmpty === 'true';
    const isLoggedin = isLoggedinRaw === 'true' || (userId && userId !== '');

    // URL設定
    const deleteBase = cartData.dataset.deleteBase || '/home/cart/delete-ajax/';
    const deleteTemp = cartData.dataset.deleteTemp || '/home/cart/delete-temp/';
    const updateBase = cartData.dataset.updateBase || '/home/cart/update-quantity/';
    const updateTemp = cartData.dataset.updateTemp || '/home/cart/update-temp-quantity/';

    // デバッグ情報
    console.log('=== カートページ 初期化 ===');
    console.log('ログイン状態 (raw):', isLoggedinRaw);
    console.log('ユーザーID:', userId);
    console.log('ログイン状態 (判定後):', isLoggedin);
    console.log('カートが空:', isEmpty);
    console.log('削除URL（ログイン）:', deleteBase);
    console.log('削除URL（未ログイン）:', deleteTemp);

    /**
     * カートアイテムを削除する
     *
     * @param {number|string} indexOrId - ログイン時はcartItemId、未ログイン時はindex
     */
    window.deleteCartItem = function(indexOrId) {

        console.log('削除処理開始 - ターゲット:', indexOrId, 'ログイン状態:', isLoggedin);
        //ログイン状態を判断する
        if (isLoggedin) {
            // cartItemIdが空の場合はエラー
            if (!indexOrId || indexOrId === '') {
                console.error('削除エラー: cartItemIdが空です。ログインユーザーにはcartItemIdが必要です。');
                alert('エラー: 商品情報を再読み込みしてください');
                return;
            }

            fetch(deleteBase + indexOrId, {
                method: 'POST'
            })
                .then(response => {
                    if (!response.ok) throw new Error('サーバーエラー: ' + response.status);
                    return response.json();
                })
                .then(data => {
                    if (data.success) {
                        // 対応する製品DOMを削除
                        const item = document.querySelector(`[data-cart-item-id="${indexOrId}"]`)
                            || document.querySelector(`[data-index="${indexOrId}"]`);

                        if (item) {
                            item.closest('.cart-item').remove();
                        }

                        refreshCartUI(data);

                    } else {
                        console.error('削除エラー:', data.message);
                        alert('削除に失敗しました: ' + data.message);
                    }
                })
                .catch(error => {
                    console.error('削除エラー:', error);
                    alert('削除エラー: ' + error.message);
                });
        } else {
            //未登録状態時実行
            fetch(deleteTemp + indexOrId, {
                method: 'POST'
            })
                .then(response => {
                    if (!response.ok) throw new Error('サーバーエラー: ' + response.status);
                    return response.json();
                })
                .then(data => {
                    if (data.success) {
                        // 対応する製品DOMを削除
                        const item = document.querySelector(`[data-cart-item-id="${indexOrId}"]`)
                            || document.querySelector(`[data-index="${indexOrId}"]`);

                        if (item) {
                            item.closest('.cart-item').remove();
                        }

                        refreshCartUI(data);

                    } else {
                        console.error('削除エラー:', data.message);
                        alert('削除に失敗しました: ' + data.message);
                    }
                })
                .catch(error => {
                    console.error('削除エラー:', error);
                    alert('削除エラー: ' + error.message);
                });
        }
    };

    /**
     * カートアイテムの数量を更新する
     *
     * @param {number|string} indexOrId - ログイン時はcartItemId、未ログイン時はindex
     * @param {number} quantity - 更新後の数量
     */
    window.updateQuantity = function(indexOrId, quantity) {

        if (quantity < 1) return;
        // リクエスト処理中は、追加のリクエストを受け付けずに無視する制御を行う
        if (isUpdating) return;

        isUpdating = true;

        console.log('数量更新処理開始 - ターゲット:', indexOrId, '数量:', quantity, 'ログイン状態:', isLoggedin);

        if (isLoggedin) {
            // cartItemIdが空の場合はエラー
            if (!indexOrId || indexOrId === '') {
                console.error('更新エラー: cartItemIdが空です。ログインユーザーにはcartItemIdが必要です。');
                alert('エラー: 商品情報を再読み込みしてください');

                return;
            }

            fetch(updateBase + indexOrId, {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: new URLSearchParams({ 'quantity': quantity })
            })
                .then(response => {
                    if (!response.ok) throw new Error('サーバーエラー: ' + response.status);
                    return response.json();
                })
                .then(data => {
                    if (data.success) {
                        // 商品合計を更新
                        if (data.itemTotal) {
                            const subtotal = document.querySelector(`#item-total-${indexOrId}`);
                            if (subtotal) {
                                subtotal.innerText = data.itemTotal;
                            }
                        }

                        refreshCartUI(data);

                    } else {
                        console.error('更新エラー:', data.message);
                        alert('更新に失敗しました: ' + data.message);
                    }
                    isUpdating = false;
                })
                .catch(error => {
                    console.error('更新エラー:', error);
                    alert('更新エラー: ' + error.message);
                });
        } else {
            fetch(updateTemp, {
                //未ログインユーザー：インデックスで更新
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: new URLSearchParams({ 'index': indexOrId, 'quantity': quantity })
            })
                .then(response => {
                    console.log('更新レスポンス:', response.status);
                    if (!response.ok) throw new Error('サーバーエラー: ' + response.status);
                    return response.json();
                })
                .then(data => {
                    console.log('更新結果:', data);
                    if (data.success) {
                        // 商品合計を更新
                        if (data.itemTotal) {
                            const subtotal = document.querySelector(`#item-total-${indexOrId}`);
                            if (subtotal) {
                                subtotal.innerText = data.itemTotal;
                            }
                        }

                        refreshCartUI(data);

                    } else {
                        console.error('更新エラー:', data.message);
                        alert('更新に失敗しました: ' + data.message);
                    }
                    isUpdating = false;
                })
                .catch(error => {
                    console.error('更新エラー:', error);
                    alert('更新エラー: ' + error.message);
                    isUpdating = false;
                });
        }
    };

    // カートアイテムにイベントリスナーを追加
    const cartItems = document.querySelectorAll('.cart-item > div'); //  子要素を取得
    console.log('カートアイテム数:', cartItems.length);

    cartItems.forEach((item, index) => {
        // cartItemId と index の両方を取得
        const cartItemId = item.getAttribute('data-cart-item-id') || '';
        const itemIndex = item.getAttribute('data-index') || index;

        console.log(`カートアイテム[${index}]: cartItemId =`, cartItemId || '空', ', index =', itemIndex);

        // 削除ボタン
        const deleteBtn = item.querySelector('.btn-delete');
        if (deleteBtn) {
            deleteBtn.addEventListener('click', function() {
                const targetId = isLoggedin ? cartItemId : index;
                deleteCartItem(targetId);
            });
        }

        // 数量入力
        const quantityInput = item.querySelector('input[type="number"]');
        if (quantityInput) {
            quantityInput.addEventListener('input', debounce(function() {
                const targetId = isLoggedin ? cartItemId : itemIndex;
                updateQuantity(targetId, parseInt(this.value));
            }, 400));
        }
    });

    console.log('=== カートページ JavaScript 読み込み完了 ===');
});