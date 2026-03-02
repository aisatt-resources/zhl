package aisato.ec.service.serviceImpl;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import aisato.ec.dto.LoginRequest;
import aisato.ec.dto.RegisterRequest;
import aisato.ec.entity.User;
import aisato.ec.entity.UserAddress;
import aisato.ec.mapper.UserMapper;
import aisato.ec.mapper.UserPaymentSettingMapper;
import aisato.ec.service.UserAddressService;
import aisato.ec.service.UserService;

/**
 * ユーザーサービス実装クラス
 * ユーザー関連のビジネスロジックを実装
 */
@Service
public class UserServiceImpl implements UserService {
    
	/**
	 * ユーザーマーピング側呼び出す
	 */
    @Autowired
    private UserMapper userMapper;
    
    /**
	 * ユーザー住所サービス側呼び出す
	 */
    @Autowired
    private UserAddressService userAddressService;
    
    /**
	 *  ユーザー支払い設定マーケティング側呼び出す
	 */
    @Autowired
    private UserPaymentSettingMapper userPaymentSettingMapper;
    /**
     * パスワードをハッシュ化
     * @param password パスワード
     * @return ハッシュ化されたパスワード（形式: ソルト:ハッシュ）
     */
    private String hashPassword(String password) {
        try {
            
            byte[] salt = new byte[16];
            SecureRandom random = new SecureRandom();
            random.nextBytes(salt);
            
            // SHA-256でハッシュ化
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes(StandardCharsets.UTF_8));
            
            // 結合して返す
            String saltBase64 = Base64.getEncoder().encodeToString(salt);
            String hashBase64 = Base64.getEncoder().encodeToString(hashedPassword);
            
            return saltBase64 + ":" + hashBase64;
            
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("パスワードのハッシュ化に失敗しました", e);
        }
    }
    
    /**
     * パスワードを検証
     * @param rawPassword 入力されたパスワード
     * @param storedPassword データベースに保存されたハッシュ化されたパスワード
     * @return 一致する場合はtrue
     */
    private boolean checkPassword(String rawPassword, String storedPassword) {
        try {
            // 保存されたパスワードを分割
            String[] parts = storedPassword.split(":");
            if (parts.length != 2) {
                return false;
            }
            
            String saltBase64 = parts[0];
            String hashBase64 = parts[1];
            
            byte[] salt = Base64.getDecoder().decode(saltBase64);
            
            // 入力されたパスワードを同じ方法でハッシュ化
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashedPassword = md.digest(rawPassword.getBytes(StandardCharsets.UTF_8));
            String inputHashBase64 = Base64.getEncoder().encodeToString(hashedPassword);
            
            // ハッシュを比較
            return MessageDigest.isEqual(
                Base64.getDecoder().decode(hashBase64),
                Base64.getDecoder().decode(inputHashBase64)
            );
            
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("パスワードの検証に失敗しました", e);
        }
    }
    
    /**
     * ユーザー認証
     *
     * @param request ログインリクエスト情報
     * @return 認証成功したユーザー情報
     * @throws Exception 認証失敗時（ユーザーが存在しない、パスワード不一致、無効アカウント）
     */
    @Override
    public User authenticate(LoginRequest request) throws Exception {
        // ユーザー名またはメールアドレスでユーザーを検索
        User user = userMapper.findByUsernameOrEmail(request.getUsername());
        
        if (user == null) {
            throw new Exception("ユーザー名またはパスワードが正しくありません");
        }
        
        // パスワードを検証
        if (!checkPassword(request.getPassword(), user.getPassword())) {
            throw new Exception("ユーザー名またはパスワードが正しくありません");
        }
        
        // ステータスを確認
        if (user.getStatus() == 0) {
            throw new Exception("このアカウントは無効です");
        }
        
        return user;
    }
    
    /**
     * ユーザー登録
     *
     * @param request 登録リクエスト情報
     * @return 登録されたユーザー情報
     * @throws Exception 重複ユーザー名やメールの場合
     */
    @Override
    @Transactional
    public User register(RegisterRequest request) throws Exception {
        // ユーザー名の重複チェック
        User existingUser = userMapper.findByUsernameOrEmail(request.getUsername());
        if (existingUser != null) {
            throw new Exception("このユーザー名は既に使用されています");
        }
        
        // メールアドレスの重複チェック
        existingUser = userMapper.findByUsernameOrEmail(request.getEmail());
        if (existingUser != null) {
            throw new Exception("このメールアドレスは既に登録されています");
        }
        
        // 新規ユーザーを作成
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(hashPassword(request.getPassword())); // パスワードをハッシュ化
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setRealName(request.getRealName());
        user.setStatus(1); // 有効
        
        // データベースに登録
        userMapper.insert(user);
        
        return user;
    }
    
    /**
     * ユーザー情報を取得
     *
     * @param userId ユーザーID
     * @return ユーザー情報
     */
    @Override
    public User getUserInfo(Long userId) {
        return userMapper.findById(userId);
    }
    
    /**
     * ユーザー情報を更新
     *
     * @param user 更新するユーザー情報
     */
    @Override
    public void updateUserInfo(User user) {
     userMapper.update(user);
    }
    
    /**
     * ユーザーの住所一覧を取得
     *
     * @param userId ユーザーID
     * @return 住所リスト
     */
    @Override
    public List<UserAddress> getUserAddresses(Long userId) {
        return userAddressService.getUserAddresses(userId);
    }
    
    /**
     * IDで住所を取得
     *
     * @param addressId 住所ID
     * @return 住所情報
     */
    @Override
    public UserAddress getAddressById(Long addressId) {
        return userAddressService.getAddressById(addressId);
    }
    
    /**
     * 住所を追加
     *
     * @param address 追加する住所情報
     */
    @Override
    public void addUserAddress(UserAddress address) {
        userAddressService.addUserAddress(address);
    }
    
    /**
     * 住所を更新
     *
     * @param address 更新する住所情報
     */
    @Override
    public void updateUserAddress(UserAddress address) {
        userAddressService.updateUserAddress(address);
    }
    
    /**
     * 住所を削除
     *
     * @param addressId 住所ID
     * @param userId ユーザーID
     */
    @Override
    public void deleteUserAddress(Long addressId, Long userId) {
        userAddressService.deleteUserAddress(addressId, userId);
    }
    
    /**
     * デフォルト住所を設定
     *
     * @param addressId 住所ID
     * @param userId ユーザーID
     */
    @Override
    public void setDefaultAddress(Long addressId, Long userId) {
        userAddressService.setDefaultAddress(addressId, userId);
    }
    
    /**
     * ユーザーのデフォルト支払い方法IDを取得
     *
     * @param userId ユーザーID
     * @return デフォルト支払い方法ID、存在しない場合はnull
     * @throws IllegalArgumentException userIdがnullの場合
     */
    @Override
    public Long getDefaultPaymentMethodId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("ユーザーIDが無効です");
        }
        
        // user_payment_settingsテーブルからデフォルト支払い方法を取得
        Long defaultMethodId = userPaymentSettingMapper.findDefaultMethodIdByUserId(userId);
        
        return defaultMethodId;
    }
}