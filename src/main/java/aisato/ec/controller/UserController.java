package aisato.ec.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import aisato.ec.dto.LoginRequest;
import aisato.ec.dto.RegisterRequest;
import aisato.ec.entity.CartItem;
import aisato.ec.entity.User;
import aisato.ec.entity.UserAddress;
import aisato.ec.service.CartService;
import aisato.ec.service.UserService;
import lombok.extern.slf4j.Slf4j;

/**
 * ユーザーコントローラー
 * ユーザー認証および個人情報・住所管理関連のリクエストを処理
 * 
 * ログイン/ログアウト、会員登録、プロフィール表示・更新、
 * 住所の追加/編集/削除/デフォルト設定などを提供
 * 
 * @author zhl
 * @version 1.0.0
 */
@Controller
@Slf4j
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private CartService cartService;

	/**
	* ログインページ表示
	* 既にログイン済みの場合はホームページにリダイレクト
	* 
	* @param model モデル
	* @param session セッション情報
	* @return ログインビュー名 またはリダイレクトURL
	*/
	@GetMapping("/login")
	public String loginPage(Model model, HttpSession session) {

		User currentUser = (User) session.getAttribute("currentUser");

		if (currentUser != null) {
			return "redirect:/";
		}

		model.addAttribute("loginRequest", new LoginRequest());
		return "user/login";
	}

	/**
	* ログイン処理
	* 認証成功時、一時カートを正式カートに統合
	* リダイレクト先が指定されていればそちらに遷移
	* 
	* @param request ログインフォームの情報
	* @param model モデル
	* @param session セッション情報
	* @return ログイン成功後のリダイレクト先 またはログインビュー
	*/
	@PostMapping("/login")
	public String login(@ModelAttribute LoginRequest request, Model model, HttpSession session) {
		try {
			// ユーザー認証
			User user = userService.authenticate(request);
			//  セッションにユーザー情報を保存
			session.setAttribute("currentUser", user);
			log.info("ログイン成功 - ユーザー: {}", user.getUsername());

			// 一時カートを正式カートに統合
			@SuppressWarnings("unchecked")
			List<CartItem> tempCart = (List<CartItem>) session.getAttribute("tempCart");
			if (tempCart != null && !tempCart.isEmpty()) {
				for (CartItem item : tempCart) {
					try {
						// 重複チェック付きでカートに追加
						cartService.addToCart(
								user.getUserId(),
								item.getProductId(),
								item.getSelectedColor(),
								item.getSelectedSize(),
								item.getQuantity());
					} catch (Exception e) {
						// 重複エラーなどは無視
						log.error("カート統合エラー: {}", e.getMessage());
					}
				}
				// 一時カートをクリア
				session.removeAttribute("tempCart");
				log.info("一時カートを正式カートに統合 - アイテム数: {}", tempCart.size());
			}

			// リダイレクト先を取得
			String redirectUrl = (String) session.getAttribute("redirectUrl");
			if (redirectUrl != null) {
				session.removeAttribute("redirectUrl");
				return "redirect:" + redirectUrl;
			}

			return "redirect:/cart"; // ログイン後はカートページにリダイレクト（親切な体験）
		} catch (Exception e) {
			model.addAttribute("error", e.getMessage());
			model.addAttribute("loginRequest", request);
			return "user/login";
		}
	}

	/**
	 * 会員登録ページ表示
	 * 既にログイン済みの場合はホームページにリダイレクト
	 * 
	 * @param model モデル
	 * @param session セッション情報
	 * @return 会員登録ビュー名 またはリダイレクトURL
	 */
	@GetMapping("/register")
	public String registerPage(Model model, HttpSession session) {
		// 既にログインしている場合は首页にリダイレクト
		User currentUser = (User) session.getAttribute("currentUser");
		if (currentUser != null) {
			return "redirect:/";
		}

		model.addAttribute("registerRequest", new RegisterRequest());
		return "user/register";
	}

	/**
	* 会員登録処理
	* 検証エラー時は再度フォーム表示
	* 登録成功時はログイン状態としてホームページにリダイレクト
	* 
	* @param request 登録フォーム情報
	* @param result 検証結果
	* @param model モデル
	* @param session セッション情報
	* @return 登録成功後のリダイレクト先 または登録フォームビュー
	*/
	@PostMapping("/register")
	public String register(@Valid @ModelAttribute RegisterRequest request,
			BindingResult result,
			Model model, HttpSession session) {
		// 検証エラーがある場合
		if (result.hasErrors()) {
			return "user/register";
		}

		try {
			// ユーザー登録
			User user = userService.register(request);

			// セッションにユーザー情報を保存
			session.setAttribute("currentUser", user);

			return "redirect:/";
		} catch (Exception e) {
			model.addAttribute("error", e.getMessage());
			model.addAttribute("registerRequest", request);
			return "user/register";
		}
	}

	/**
	* ログアウト処理
	* セッションを破棄してホームページにリダイレクト
	* 
	* @param session セッション情報
	* @return ホームページリダイレクトURL
	*/
	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/";
	}

	/**
	 * 個人情報ページ表示
	 * 未ログインの場合はログインページにリダイレクト
	 * 
	 * @param model モデル
	 * @param session ユーザーセッション情報
	 * @return プロフィールビュー名 またはリダイレクトURL
	 */
	@GetMapping("/user/profile")
	public String profilePage(Model model, HttpSession session) {
		User currentUser = (User) session.getAttribute("currentUser");

		// 未ログインの場合はログインページにリダイレクト
		if (currentUser == null) {
			session.setAttribute("redirectUrl", "/user/profile");
			return "redirect:/login";
		}

		// ユーザー情報を取得
		User user = userService.getUserInfo(currentUser.getUserId());
		model.addAttribute("user", user);

		// ユーザー住所を取得
		List<UserAddress> addresses = userService.getUserAddresses(currentUser.getUserId());
		model.addAttribute("addresses", addresses);

		// デフォルト住所を取得
		UserAddress defaultAddress = addresses.stream()
				.filter(addr -> addr.getIsDefault() != null && addr.getIsDefault() == 1)
				.findFirst()
				.orElse(null);
		model.addAttribute("defaultAddress", defaultAddress);
		model.addAttribute("currentUser", currentUser);
		return "user/profile";
	}

	/**
	* 個人情報更新処理
	* 
	* @param user 更新情報
	* @param session セッション情報
	* @return Map<String,Object> 処理結果(success, message)
	*/
	@PostMapping("/user/profile/update")
	@ResponseBody
	public Map<String, Object> updateProfile(
			@ModelAttribute User user,
			HttpSession session) {

		Map<String, Object> result = new HashMap<>();

		try {
			User currentUser = (User) session.getAttribute("currentUser");

			if (currentUser == null) {
				result.put("success", false);
				result.put("message", "ログインが必要です");
				return result;
			}

			// ユーザーIDを設定
			user.setUserId(currentUser.getUserId());

			// 個人情報を更新
			userService.updateUserInfo(user);

			User updatedUser = (User) session.getAttribute("currentUser");
			System.out.println("更新後のユーザー名: " + updatedUser.getUsername());

			// セッションのユーザー情報を更新
			session.setAttribute("currentUser", updatedUser);

			result.put("success", true);
			result.put("message", "個人情報を更新しました");
		} catch (Exception e) {
			result.put("success", false);
			result.put("message", e.getMessage());
		}

		return result;
	}

	/**
	 * 住所追加ページを表示
	 * 
	 * @param model モデル
	 * @param　session セッション情報
	 * @return 新しいUserAddressを登録
	 */
	@GetMapping("/user/address/add")
	public String addAddressPage(Model model, HttpSession session) {
		User currentUser = (User) session.getAttribute("currentUser");

		if (currentUser == null) {
			return "redirect:/login";
		}

		model.addAttribute("address", new UserAddress());
		model.addAttribute("currentUser", currentUser);

		return "user/address_form";
	}

	/**
	 * 住所更新ページを表示
	 * 
	 * @param addressId 住所ID
	 * @param model モデル
	 * @param　session セッション情報
	 * @return 新しいUserAddress情報を更新
	 */
	@GetMapping("/user/address/edit/{addressId}")
	public String editAddressPage(@PathVariable Long addressId,
			Model model, HttpSession session) {
		User currentUser = (User) session.getAttribute("currentUser");

		if (currentUser == null) {
			return "redirect:/login";
		}

		UserAddress address = userService.getAddressById(addressId);

		// 住所が存在しない、または自分の住所でない場合
		if (address == null || !address.getUserId().equals(currentUser.getUserId())) {
			return "redirect:/user/profile";
		}
		//　画面に値を渡す
		model.addAttribute("address", address);
		model.addAttribute("currentUser", currentUser);
		return "user/address_form";
	}

	/**
	 * 住所保存処理
	 * 
	 * @param address 住所データ
	 * @param　session セッション情報
	 * @return 更新した個人情報を保存
	 */
	@PostMapping("/user/address/save")
	public String saveAddress(@ModelAttribute UserAddress address,
			HttpSession session) {
		User currentUser = (User) session.getAttribute("currentUser");

		if (currentUser == null) {
			return "redirect:/login";
		}

		// ユーザーIDを設定
		address.setUserId(currentUser.getUserId());

		try {
			if (address.getAddressId() == null) {
				// 新規追加
				userService.addUserAddress(address);
			} else {
				// 更新
				userService.updateUserAddress(address);
			}
		} catch (Exception e) {
			// エラーメッセージを表示
			return "redirect:/user/profile?error=" + e.getMessage();
		}

		return "redirect:/user/profile";
	}

	/**
	 * 住所削除処理
	 * 
	 * @param addressId 住所ID
	 * @param　session セッション情報
	 * @return 住所削除
	 */
	@GetMapping("/user/address/delete/{addressId}")
	public String deleteAddress(@PathVariable Long addressId, HttpSession session) {
		User currentUser = (User) session.getAttribute("currentUser");

		if (currentUser == null) {
			return "redirect:/login";
		}

		try {
			userService.deleteUserAddress(addressId, currentUser.getUserId());
		} catch (Exception e) {
			// エラーメッセージを表示
			return "redirect:/user/profile?error=" + e.getMessage();
		}

		return "redirect:/user/profile";
	}

	/**
	 * デフォルト住所設定
	 * 
	 * @param addressId 住所ID
	 * @param　session セッション情報
	 * @return Map<String,Object> 処理結果(success, message)
	 */
	@PostMapping("/user/address/set-default/{addressId}")
	@ResponseBody
	public Map<String, Object> setDefaultAddress(@PathVariable Long addressId,
			HttpSession session) {
		Map<String, Object> result = new HashMap<>();

		try {
			User currentUser = (User) session.getAttribute("currentUser");
			if (currentUser == null) {
				result.put("success", false);
				result.put("message", "ログインが必要です");
				return result;
			}

			userService.setDefaultAddress(addressId, currentUser.getUserId());
			result.put("success", true);
			result.put("message", "デフォルト住所を設定しました");
		} catch (Exception e) {
			result.put("success", false);
			result.put("message", e.getMessage());
		}

		return result;
	}
}