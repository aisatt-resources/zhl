package aisato.ec.service.serviceImpl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import aisato.ec.entity.UserAddress;
import aisato.ec.mapper.UserAddressMapper;
import aisato.ec.service.UserAddressService;

/**
 * ユーザー住所サービス実装クラス
 * ユーザー住所関連のビジネスロジックを実装
 */
@Service
public class UserAddressServiceImpl implements UserAddressService {
    
	/**
	 * ユーザー住所マーピング側呼び出す
	 */
    @Autowired
    private UserAddressMapper userAddressMapper;
    
    /**
     * ユーザーの住所一覧を取得
     */
    @Override
    public List<UserAddress> getUserAddresses(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("ユーザーIDが無効です");
        }
        
        List<UserAddress> addresses = userAddressMapper.findByUserId(userId);
        
        if (addresses == null || addresses.isEmpty()) {
            return List.of(); // 空のリストを返す
        }
        
        return addresses;
    }
    
    /**
     * 住所を取得
     */
    @Override
    public UserAddress getAddressById(Long addressId) {
        if (addressId == null) {
            throw new IllegalArgumentException("住所IDが無効です");
        }
        
        UserAddress address = userAddressMapper.findById(addressId);
        
        if (address == null) {
            throw new RuntimeException("住所が見つかりません。住所ID: " + addressId);
        }
        
        return address;
    }
    
    /**
     * 住所を追加
     */
    @Override
    @Transactional
    public UserAddress addUserAddress(UserAddress address) {
        // パラメータバリデーション
        validateAddress(address, true);
        
        // デフォルト住所が設定されている場合、他の住所のデフォルト設定を解除
        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            userAddressMapper.clearDefaultAddresses(address.getUserId());
        }
        
        // 作成日時を設定
        address.setCreateTime(LocalDateTime.now());
        address.setUpdateTime(LocalDateTime.now());
        
        // データベースに登録
        userAddressMapper.insert(address);
        
        return address;
    }
    
    /**
     * 住所を更新
     */
    @Override
    @Transactional
    public UserAddress updateUserAddress(UserAddress address) {
        // パラメータバリデーション
        validateAddress(address, false);
        
        // 既存の住所を取得
        UserAddress existingAddress = getAddressById(address.getAddressId());
        
        // 権限チェック（自分の住所のみ更新可能）
        if (!existingAddress.getUserId().equals(address.getUserId())) {
            throw new RuntimeException("この住所を更新する権限がありません");
        }
        
        // デフォルト住所が設定されている場合、他の住所のデフォルト設定を解除
        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            userAddressMapper.clearDefaultAddresses(address.getUserId());
        }
        
        // 住所情報を更新
        existingAddress.setReceiverName(address.getReceiverName());
        existingAddress.setPhone(address.getPhone());
        existingAddress.setProvince(address.getProvince());
        existingAddress.setCity(address.getCity());
        existingAddress.setDistrict(address.getDistrict());
        existingAddress.setDetailAddress(address.getDetailAddress());
        existingAddress.setPostalCode(address.getPostalCode());
        existingAddress.setIsDefault(address.getIsDefault());
        existingAddress.setUpdateTime(LocalDateTime.now());
        
        // データベースを更新
        int result = userAddressMapper.update(existingAddress);
        
        if (result == 0) {
            throw new RuntimeException("住所の更新に失敗しました");
        }
        
        return existingAddress;
    }
    
    /**
     * 住所を削除
     */
    @Override
    @Transactional
    public void deleteUserAddress(Long addressId, Long userId) {
        if (addressId == null) {
            throw new IllegalArgumentException("住所IDが無効です");
        }
        
        if (userId == null) {
            throw new IllegalArgumentException("ユーザーIDが無効です");
        }
        
        // 住所を取得
        UserAddress address = getAddressById(addressId);
        
        // 権限チェック（自分の住所のみ削除可能）
        if (!address.getUserId().equals(userId)) {
            throw new RuntimeException("この住所を削除する権限がありません");
        }
        
        // デフォルト住所を削除する場合、別の住所をデフォルトに設定
        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            List<UserAddress> addresses = userAddressMapper.findByUserId(userId);
            
            // 他の住所がある場合、最初の住所をデフォルトに設定
            if (addresses != null && !addresses.isEmpty()) {
                for (UserAddress addr : addresses) {
                    if (!addr.getAddressId().equals(addressId)) {
                        addr.setIsDefault(1);
                        userAddressMapper.update(addr);
                        break;
                    }
                }
            }
        }
        
        // 住所を削除
        int result = userAddressMapper.delete(addressId);
        
        if (result == 0) {
            throw new RuntimeException("住所の削除に失敗しました");
        }
    }
    
    /**
     * デフォルト住所を設定
     */
    @Override
    @Transactional
    public void setDefaultAddress(Long addressId, Long userId) {
        if (addressId == null) {
            throw new IllegalArgumentException("住所IDが無効です");
        }
        
        if (userId == null) {
            throw new IllegalArgumentException("ユーザーIDが無効です");
        }
        
        // 住所の存在チェック
        if (!existsAddress(addressId, userId)) {
            throw new RuntimeException("住所が見つかりません、または権限がありません");
        }
        
        // 他の住所のデフォルト設定を解除
        userAddressMapper.clearDefaultAddresses(userId);
        
        // 指定した住所をデフォルトに設定
        UserAddress address = new UserAddress();
        address.setAddressId(addressId);
        address.setIsDefault(1);
        
        int result = userAddressMapper.updateDefaultFlag(address);
        
        if (result == 0) {
            throw new RuntimeException("デフォルト住所の設定に失敗しました");
        }
    }
    
    /**
     * ユーザーのデフォルト住所を取得
     */
    @Override
    public UserAddress getDefaultAddress(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("ユーザーIDが無効です");
        }
        
        List<UserAddress> addresses = userAddressMapper.findByUserId(userId);
        
        if (addresses == null || addresses.isEmpty()) {
            return null;
        }
        
        // デフォルト住所を検索
        for (UserAddress address : addresses) {
            if (address.getIsDefault() != null && address.getIsDefault() == 1) {
                return address;
            }
        }
        
        // デフォルト住所がない場合は最初の住所を返す
        return addresses.get(0);
    }
    
    /**
     * 住所の存在チェック
     */
    @Override
    public boolean existsAddress(Long addressId, Long userId) {
        if (addressId == null || userId == null) {
            return false;
        }
        
        try {
            UserAddress address = getAddressById(addressId);
            return address != null && address.getUserId().equals(userId);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 住所情報のチェック
     * @param address 住所エンティティ
     * @param isNew 新規登録の場合はtrue、更新の場合はfalse
     */
    private void validateAddress(UserAddress address, boolean isNew) {
        if (address == null) {
            throw new IllegalArgumentException("住所情報が空です");
        }
        
        if (isNew && address.getUserId() == null) {
            throw new IllegalArgumentException("ユーザーIDが必須です");
        }
        
        // 受取人氏名のバリデーション
        if (address.getReceiverName() == null || address.getReceiverName().trim().isEmpty()) {
            throw new IllegalArgumentException("受取人氏名を入力してください");
        }
        
        if (address.getReceiverName().length() > 50) {
            throw new IllegalArgumentException("受取人氏名は50文字以内で入力してください");
        }
        
        // 電話番号のバリデーション
        if (address.getPhone() == null || address.getPhone().trim().isEmpty()) {
            throw new IllegalArgumentException("電話番号を入力してください");
        }
        
        // 電話番号の形式チェック（数字とハイフンのみを許可）
        if (!address.getPhone().matches("^[0-9\\-]+$")) {
            throw new IllegalArgumentException("電話番号は数字とハイフンのみで入力してください");
        }
        
        if (address.getPhone().length() > 20) {
            throw new IllegalArgumentException("電話番号は20文字以内で入力してください");
        }
        
        // 都道府県のバリデーション
        if (address.getProvince() == null || address.getProvince().trim().isEmpty()) {
            throw new IllegalArgumentException("都道府県を入力してください");
        }
        
        if (address.getProvince().length() > 50) {
            throw new IllegalArgumentException("都道府県は50文字以内で入力してください");
        }
        
        // 市区町村のバリデーション
        if (address.getCity() == null || address.getCity().trim().isEmpty()) {
            throw new IllegalArgumentException("市区町村を入力してください");
        }
        
        if (address.getCity().length() > 50) {
            throw new IllegalArgumentException("市区町村は50文字以内で入力してください");
        }
        
        // 区/郡のバリデーション
        if (address.getDistrict() == null || address.getDistrict().trim().isEmpty()) {
            throw new IllegalArgumentException("区/郡を入力してください");
        }
        
        if (address.getDistrict().length() > 50) {
            throw new IllegalArgumentException("区/郡は50文字以内で入力してください");
        }
        
        // 詳細住所のバリデーション
        if (address.getDetailAddress() == null || address.getDetailAddress().trim().isEmpty()) {
            throw new IllegalArgumentException("詳細住所を入力してください");
        }
        
        if (address.getDetailAddress().length() > 200) {
            throw new IllegalArgumentException("詳細住所は200文字以内で入力してください");
        }
        
        // 郵便番号のバリデーション（入力されている場合）
        if (address.getPostalCode() != null && !address.getPostalCode().trim().isEmpty()) {
            // 郵便番号の形式チェック（例: 123-4567）
            if (!address.getPostalCode().matches("^\\d{3}-?\\d{4}$")) {
                throw new IllegalArgumentException("郵便番号は正しい形式で入力してください（例: 123-4567）");
            }
        }
    }
}