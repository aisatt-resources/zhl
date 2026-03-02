package aisato.ec.service;

import java.util.List;

import aisato.ec.dto.LoginRequest;
import aisato.ec.dto.RegisterRequest;
import aisato.ec.entity.User;
import aisato.ec.entity.UserAddress;

/**
 * ユーザーサービスインターフェース
 */
public interface UserService {
    
    /**
     * ユーザー認証
     * @param request ログインリクエスト
     * @return 認証されたユーザーエンティティ
     * @throws Exception 認証失敗時
     */
    User authenticate(LoginRequest request) throws Exception;
    
    /**
     * ユーザー登録
     * @param request 会員登録リクエスト
     * @return 登録されたユーザーエンティティ
     * @throws Exception 既に存在する場合
     */
    User register(RegisterRequest request) throws Exception;
    
    /**
     * ユーザー情報を取得
     * @param userId ユーザーID
     * @return ユーザーエンティティ
     */
    User getUserInfo(Long userId);
    
    /**
     * ユーザー情報を更新
     * @param user ユーザーエンティティ
     */
    void updateUserInfo(User user);
    
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
     */
    void addUserAddress(UserAddress address);
    
    /**
     * 住所を更新
     * @param address 住所エンティティ
     */
    void updateUserAddress(UserAddress address);
    
    /**
     * 住所を削除
     * @param addressId 住所ID
     * @param userId ユーザーID
     */
    void deleteUserAddress(Long addressId, Long userId);
    
    /**
     * デフォルト住所を設定
     * @param addressId 住所ID
     * @param userId ユーザーID
     */
    void setDefaultAddress(Long addressId, Long userId);
    /**
     * ユーザーのデフォルト支払い方法IDを取得
     * @param userId ユーザーID
     * @return デフォルト支払い方法ID（存在しない場合はnull）
     */
    Long getDefaultPaymentMethodId(Long userId);
}