// ドキュメントが読み込まれた後に実行
document.addEventListener('DOMContentLoaded', function() {
    console.log('商品詳細ページが読み込まれました');

	// 数量入力のチェック
	// 数量の最小値を1、最大値を10に制限
    const quantityInput = document.querySelector('input[name="quantity"]');
    if (quantityInput) {
        quantityInput.addEventListener('change', function() {
            const value = parseInt(this.value);
            if (value < 1) {
                this.value = 1;
            } else if (value > 10) {
                this.value = 10;
            }
        });
    }
});

/**
 * 商品詳細ページのJavaScript
 * - 色・サイズオプションの処理
 * - 商品画像エラー時の代替表示
 */

/**
 * 安全なJSONパース関数
 * 複数形式のJSON文字列を想定し、可能な限り配列として返す
 * @param {string} jsonStr - パース対象のJSON文字列
 * @returns {Array|Object} - パース結果、失敗時は空配列
 */
function safeJsonParse(jsonStr) {
    if (!jsonStr || jsonStr === '[]') {
        return [];
    }

    try {
        // 1. まず標準的なJSONとしてパースを試みる
        return JSON.parse(jsonStr);
    } catch (e1) {
        try {
            // 2. 単一引用符を削除（SQLから取得した場合）
            let cleaned = jsonStr.trim().replace(/^'(.*)'$/, '$1').replace(/^"(.*)"$/, '$1');

            // 3. 余分なスペースを徹底的に削除
            cleaned = cleaned
                // 配列の開き/閉じ括弧周りのスペースを削除
                .replace(/\[\s*/g, '[')
                .replace(/\s*\]/g, ']')
                // カンマ周りのスペースを標準化
                .replace(/\s*,\s*/g, ',')
                // クォート内のスペースを削除（" ホワイト " → "ホワイト"）
                .replace(/"\s+/g, '"')
                .replace(/\s+"/g, '"')
                // 連続スペースを1つに
                .replace(/\s+/g, ' ')
                // 特殊文字のエスケープを処理
                .replace(/\\u([0-9a-fA-F]{4})/g, function(match, grp) {
                    return String.fromCharCode(parseInt(grp, 16));
                });

            // 4. 再度パースを試みる
            return JSON.parse(cleaned);
        } catch (e2) {
            console.warn('JSONパースの代替方法で成功:', cleaned);
            try {
                // 5. 最後の手段：eval（安全な環境でのみ使用）
                // 注意：実際の運用ではこの方法は避けるべきですが、
                // データが信頼できる内部システムの場合は許容範囲
                return (new Function('return ' + jsonStr))();
            } catch (e3) {
                console.error('JSONパースに完全に失敗:', jsonStr, 'エラー:', e3);
                return [];
            }
        }
    }
}

// DOMロード完了後に実行
document.addEventListener('DOMContentLoaded', function() {
    // 色オプションの処理
    const colorContainer = document.querySelector('.color-options');
    if (colorContainer) {
        const rawJson = colorContainer.getAttribute('data-options') || '[]';
        console.log('【色】生データ:', rawJson); // デバッグ用

        try {
            // 安全なJSONパース
            const colors = safeJsonParse(rawJson);
            console.log('【色】パース結果:', colors); // デバッグ用

            // オプションを動的に挿入
            if (Array.isArray(colors) && colors.length > 0) {
                colors.forEach(color => {
                    const colorTrimmed = (color || '').toString().trim();
                    if (colorTrimmed && colorTrimmed !== 'なし' && colorTrimmed !== '') {
                        const option = document.createElement('span');
                        option.className = 'color-option';
                        option.textContent = colorTrimmed;
                        option.dataset.color = colorTrimmed;

                        option.addEventListener('click', function() {
                            document.querySelectorAll('.color-option').forEach(opt => {
                                opt.classList.remove('selected');
                            });
                            this.classList.add('selected');

                            const colorInput = document.getElementById('selectedColorInput')
                            if (colorInput) {
                                colorInput.value = this.dataset.color;
                            }
                        });

                        colorContainer.appendChild(option);
                    }
                });

                // デフォルトで最初のオプションを選択
                if (colors.length > 0 && colors[0].trim() !== 'なし') {
                    const firstOption = colorContainer.querySelector('.color-option');
                    if (firstOption) {
                        firstOption.classList.add('selected');
                        const colorInput = document.getElementById('selectedColorInput')
                        if (colorInput) {
                            colorInput.value = firstOption.dataset.color;
                        }
                    }
                }
            } else {
                // オプションがない場合の表示
                const noneDiv = document.createElement('div');
                noneDiv.className = 'option-none';
                noneDiv.textContent = '選択不可';
                colorContainer.appendChild(noneDiv);
            }
        } catch (e) {
            console.error('色オプション処理エラー:', e);
            const errorDiv = document.createElement('div');
            errorDiv.className = 'option-error';
            errorDiv.textContent = '色: データ形式エラー';
            colorContainer.appendChild(errorDiv);
        }
    }

	// ===============================
	// サイズオプションの処理
	// ===============================
    const sizeContainer = document.querySelector('.size-options');
    if (sizeContainer) {
        const rawJson = sizeContainer.getAttribute('data-options') || '[]';
        console.log('【サイズ】生データ:', rawJson); // デバッグ用

        try {
            const sizes = safeJsonParse(rawJson);
            console.log('【サイズ】パース結果:', sizes); // デバッグ用

            if (Array.isArray(sizes) && sizes.length > 0) {
                sizes.forEach(size => {
                    const sizeTrimmed = (size || '').toString().trim();
                    if (sizeTrimmed && sizeTrimmed !== 'なし' && sizeTrimmed !== '') {
                        const option = document.createElement('span');
                        option.className = 'size-option';
                        option.textContent = sizeTrimmed;
                        option.dataset.size = sizeTrimmed;

                        option.addEventListener('click', function() {
                            document.querySelectorAll('.size-option').forEach(opt => {
                                opt.classList.remove('selected');
                            });
                            this.classList.add('selected');

                            const sizeInput = document.getElementById('selectedSizeInput')
                            if (sizeInput) {
                                sizeInput.value = this.dataset.size;
                            }
                        });

                        sizeContainer.appendChild(option);
                    }
                });

                // デフォルトで最初のオプションを選択
                if (sizes.length > 0 && sizes[0].trim() !== 'なし') {
                    const firstOption = sizeContainer.querySelector('.size-option');
                    if (firstOption) {
                        firstOption.classList.add('selected');
                        const sizeInput = document.getElementById('selectedSizeInput')
                        if (sizeInput) {
                            sizeInput.value = firstOption.dataset.size;
                        }
                    }
                }
            } else {
                const noneDiv = document.createElement('div');
                noneDiv.className = 'option-none';
                noneDiv.textContent = '選択不可';
                sizeContainer.appendChild(noneDiv);
            }
        } catch (e) {
            console.error('サイズオプション処理エラー:', e);
            const errorDiv = document.createElement('div');
            errorDiv.className = 'option-error';
            errorDiv.textContent = 'サイズ: データ形式エラー';
            sizeContainer.appendChild(errorDiv);
        }
    }

	// ===============================
	// 画像エラー処理
	// ===============================
    function handleImageError(img) {
        if (img.src.includes('default.jpg') || img.src.includes('no-image')) {
            img.style.display = 'none';
            const parent = img.parentElement;
            if (!parent.querySelector('.no-image-placeholder')) {
                const placeholder = document.createElement('div');
                placeholder.className = 'no-image-placeholder';
                placeholder.innerHTML = '📷<br>画像なし';
                placeholder.style.cssText = `
                    position: absolute;
                    top: 50%;
                    left: 50%;
                    transform: translate(-50%, -50%);
                    text-align: center;
                    color: #999;
                    font-size: 24px;
                `;
                parent.style.position = 'relative';
                parent.appendChild(placeholder);
            }
            return;
        }
        img.src = '/images/default.jpg';
    }

    document.querySelectorAll('.product-main-image img').forEach(img => {
        img.onerror = function() {
            handleImageError(this);
        };
    });

    // デバッグ情報の表示（開発時のみ）
    console.log('=== 商品詳細ページ JavaScript 読み込み完了 ===');
    console.log('色コンテナ:', colorContainer ? '存在' : 'なし');
    console.log('サイズコンテナ:', sizeContainer ? '存在' : 'なし');
});

