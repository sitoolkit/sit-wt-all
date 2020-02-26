package io.sitoolkit.wt.domain.tester;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.exception.ExceptionUtils;
import io.sitoolkit.wt.domain.testscript.TestStep;
import io.sitoolkit.wt.infra.VerifyException;
import lombok.Getter;
import lombok.Setter;

/**
 * テストの"一連"の実行結果を表すエンティティです。 "一連"の範囲は、{@link Tester#operate(String)} の1回の呼び出しで実行される 全ての{@link
 * TestStep}です。
 *
 * @author yuichi.kuwahara
 */
public class TestResult {

  /** 検証失敗のリスト */
  private List<VerifyException> verifyExceptions = new ArrayList<>();

  /** テスト実行失敗の原因となった例外 */
  private Throwable errorCause;

  @Getter @Setter private Path evidenceFile;

  /**
   * テスト実行が失敗した理由のメッセージ文字列を構築します。
   *
   * @return テスト実行が失敗した理由のメッセージ文字列
   */
  public String buildReason() {
    StringBuilder sb = new StringBuilder();

    sb.append("テスト実行が失敗しました。");

    if (errorCause != null) {
      sb.append("\n\t");
      sb.append(ExceptionUtils.getStackTrace(errorCause));
    }

    for (VerifyException ve : verifyExceptions) {
      sb.append("\n\t");
      sb.append(ExceptionUtils.getStackTrace(ve));
    }

    return sb.toString();
  }

  /**
   * テスト実行が成功した場合にtrueを取得します。
   *
   * @return テスト成否
   */
  public boolean isSuccess() {
    return errorCause == null && verifyExceptions.isEmpty();
  }

  /**
   * 検証失敗の例外を追加します。
   *
   * @param ve 検証失敗の例外
   */
  public void add(VerifyException ve) {
    this.verifyExceptions.add(ve);
  }

  /**
   * 検証失敗の件数を取得します。
   *
   * @return 検証失敗の件数
   */
  public int getFailCount() {
    return verifyExceptions.size();
  }

  /**
   * テスト実行失敗の原因となった例外を取得します。
   *
   * @return テスト実行失敗の原因となった例外
   */
  public Throwable getErrorCause() {
    return errorCause;
  }

  /**
   * テスト実行失敗の原因となった例外を設定します。
   *
   * @param errorCause テスト実行失敗の原因となった例外
   */
  public void setErrorCause(Throwable errorCause) {
    this.errorCause = errorCause;
  }
}
