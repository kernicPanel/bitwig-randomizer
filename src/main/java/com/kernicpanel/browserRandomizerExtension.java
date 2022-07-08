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
        .getSignalSetting("Select", "Randomize browser selection", "Select random item")
        .addSignalObserver(
            selectRandomItem(host, popupBrowser, cursorTrack, resultsItemBank, rand));
    documentState
        .getSignalSetting("Add", "Randomize browser selection", "Add current item")
        .addSignalObserver(popupBrowser::commit);
  }

  private NoArgsCallback selectRandomItem(
      ControllerHost host,
      PopupBrowser popupBrowser,
      CursorTrack cursorTrack,
      BrowserResultsItemBank resultsItemBank,
      Random rand) {
    return () -> {
      if (!popupBrowser.exists().getAsBoolean()) {
        cursorTrack.endOfDeviceChainInsertionPoint().browse();
      }

      host.scheduleTask(
          () -> {
            Integer random = rand.nextInt(popupBrowser.resultsColumn().entryCount().get());
            resultsItemBank.getItemAt(random).isSelected().set(true);
          },
          300);
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
