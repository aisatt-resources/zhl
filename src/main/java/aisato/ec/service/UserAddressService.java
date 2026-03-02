package aisato.ec.service;

import java.util.List;

import aisato.ec.entity.UserAddress;

/**
 * ユーザー住所サービスインターフェース
 */
public interface UserAddressService {
    
    /**
     * ユーザーの住所一覧を取得
     * @param userId ユーザーID
     * @return 住所リスト
     */
    List<UserAddress> getUserAddresses(Long userId);
    
    /**
     * 住所を取得
     * @param addressId 住所ID
     * @return 住所エンティティ
     */
    UserAddress getAddressById(Long addressId);
    
    /**
     * 住所を追加
     * @param address 住所エンティティ
     * @return 追加された住所エンティティ
     */
    UserAddress addUserAddress(UserAddress address);
    
    /**
     * 住所を更新
     * @param address 住所エンティティ
     * @return 更新された住所エンティティ
     */
    UserAddress updateUserAddress(UserAddress address);
    
    /**
     * 住所を削除
     * @param addressId 住所ID
     * @param userId ユーザーID（権限チェック用）
     */
    void deleteUserAddress(Long addressId, Long userId);
    
    /**
     * デフォルト住所を設定
     * @param addressId 住所ID
     * @param userId ユーザーID
     */
    void setDefaultAddress(Long addressId, Long userId);
    
    /**
     * ユーザーのデフォルト住所を取得
     * @param userId ユーザーID
     * @return デフォルト住所エンティティ（存在しない場合はnull）
     */
    UserAddress getDefaultAddress(Long userId);
    
    /**
     * 住所の存在チェック
     * @param addressId 住所ID
     * @param userId ユーザーID
     * @return 存在する場合はtrue
     */
    boolean existsAddress(Long addressId, Long userId);
}