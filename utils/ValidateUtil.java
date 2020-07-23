import java.util.regex.Pattern;

/**
 * 用于各种认证
 */
public class ValidateUtil {

  private static Pattern emailPattern = Pattern.compile("^\\w+((-\\w+)|(\\.\\w+))*@[A-Za-z0-9]+((.|-)[A-Za-z0-9]+)*\\.[A-Za-z0-9]+$");

  private static Pattern phonePattern = Pattern.compile("^((\\+86)|(86))?1\\d{10}$");

  private static Pattern namePattern = Pattern.compile("^[a-zA-Z\\d.-_@]{1,16}$");

  private static Pattern passwordPattern = Pattern.compile("^[a-zA-Z\\d_~`@!$%^&*;:'\"?()<>,.+-=]{6,20}$");
  /**
   * 邮箱认证
   * @return
   */
  public static boolean email(String email){
     return emailPattern.matcher(email).find();
  }

  /**
   * 电话号码认证
   * @return
   */
  public static boolean chinaPhone(String phone){
    return phonePattern.matcher(phone).find();
  }

  /**
   * 常用用户名认证
   * @return
   */
  public static boolean name(String name){
    return namePattern.matcher(name).find();
  }


  /**
   * 密码认证
   * @return
   */
  public static boolean password(String password){
    return passwordPattern.matcher(password).find();
  }

}
