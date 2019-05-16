package io.sitoolkit.wt.domain.debug;

import io.sitoolkit.wt.domain.testscript.Locator;

/**
 * ロケーターのチェック機能を提供するクラスが実装するインターフェースです。
 * 
 * @author yuichi.kuwahara
 *
 */
public interface LocatorChecker {

  /**
   * 表示中の画面にロケーターに該当する要素に関する情報をログに出力します。
   *
   * @param locator チェック対象のロケーター
   */
  void check(Locator locator);
}
