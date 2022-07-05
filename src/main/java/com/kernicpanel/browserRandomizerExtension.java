package com.kernicpanel;

import com.bitwig.extension.callback.NoArgsCallback;
import com.bitwig.extension.controller.api.*;
import com.bitwig.extension.controller.ControllerExtension;

import java.util.Random;

public class browserRandomizerExtension extends ControllerExtension {
  protected browserRandomizerExtension(
      final browserRandomizerExtensionDefinition definition, final ControllerHost host) {
    super(definition, host);
  }

  @Override
  public void init() {
    final ControllerHost host = getHost();

    final DocumentState documentState = host.getDocumentState();
    final PopupBrowser popupBrowser = host.createPopupBrowser();
    popupBrowser.exists().markInterested();
    final CursorTrack cursorTrack = host.createCursorTrack(0, 0);
    Random rand = new Random();

    popupBrowser.resultsColumn().entryCount().markInterested();
    BrowserResultsItemBank resultsItemBank = popupBrowser.resultsColumn().createItemBank(100000);

    documentState
        .getSignalSetting("Select", "browser", "Select random item")
        .addSignalObserver(
            selectRandomItem(host, popupBrowser, cursorTrack, resultsItemBank, rand, false));
    documentState
        .getSignalSetting("Add", "browser", "Add current item")
        .addSignalObserver(popupBrowser::commit);
    documentState
        .getSignalSetting("Random", "browser", "Surprise me!")
        .addSignalObserver(
            selectRandomItem(host, popupBrowser, cursorTrack, resultsItemBank, rand, true));
  }

  private NoArgsCallback selectRandomItem(
      ControllerHost host,
      PopupBrowser popupBrowser,
      CursorTrack cursorTrack,
      BrowserResultsItemBank resultsItemBank,
      Random rand,
      Boolean commit) {
    return () -> {
      if (!popupBrowser.exists().getAsBoolean()) {
        cursorTrack.endOfDeviceChainInsertionPoint().browse();
      }
      resultsItemBank
          .getItemAt(rand.nextInt(popupBrowser.resultsColumn().entryCount().get()))
          .isSelected()
          .set(true);

      if (commit) {
        host.scheduleTask(popupBrowser::commit, 300);
      }
    };
  }

  @Override
  public void exit() {
    // TODO: Perform any cleanup once the driver exits
  }

  @Override
  public void flush() {
    // TODO Send any updates you need here.
  }
}
