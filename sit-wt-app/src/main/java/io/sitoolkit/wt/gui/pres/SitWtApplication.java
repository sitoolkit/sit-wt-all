package io.sitoolkit.wt.gui.pres;

import java.util.ResourceBundle;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import io.sitoolkit.util.buildtoolhelper.proxysetting.ProxySettingService;
import io.sitoolkit.wt.gui.infra.config.ApplicationConfig;
import io.sitoolkit.wt.gui.infra.config.PropertyManager;
import io.sitoolkit.wt.gui.infra.fx.FxContext;
import io.sitoolkit.wt.infra.log.SitLogger;
import io.sitoolkit.wt.infra.log.SitLoggerFactory;
import io.sitoolkit.wt.util.infra.concurrent.ExecutorContainer;
import io.sitoolkit.wt.util.infra.util.SystemUtils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class SitWtApplication extends Application {

  private static final SitLogger LOG = SitLoggerFactory.getLogger(SitWtApplication.class);

  private AppController controller;

  private ConfigurableApplicationContext appCtx;

  public static void main(String[] args) {
    LOG.info("app.envInfo", SystemUtils.getEnvironmentInfo());
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) throws Exception {

    Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
      LOG.error("app.unexpectedException", throwable);
      Alert alert = new Alert(AlertType.ERROR);
      alert.setContentText("エラーが発生しました。");
      alert.show();
    });

    FxContext.setPrimaryStage(primaryStage);
    FxContext.setHostServices(getHostServices());

    ProxySettingService.getInstance().loadProxy();

    primaryStage.setTitle("SI-Toolkit for Web Testing");

    FXMLLoader loader = new FXMLLoader(getClass().getResource("/App.fxml"));
    loader.setResources(ResourceBundle.getBundle("message.message"));

    appCtx = new AnnotationConfigApplicationContext(ApplicationConfig.class);
    loader.setControllerFactory(appCtx::getBean);

    Parent root = loader.load();
    controller = loader.getController();

    primaryStage.addEventHandler(WindowEvent.WINDOW_SHOWN, event -> {
      controller.postInit();
    });

    Scene scene = new Scene(root);

    // TODO フォントファイルを直接ダウンロードすれば有効か要検証
    // scene.getStylesheets().add("http://fonts.googleapis.com/css?family=Material+Icons");

    scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
    primaryStage.setScene(scene);
    primaryStage.getIcons().add(new Image("/icon/sitoolkit.png"));
    primaryStage.show();

  }

  @Override
  public void stop() throws Exception {
    controller.destroy();
    appCtx.close();
    PropertyManager.get().save();
    ExecutorContainer.get().shutdown();
  }

}
