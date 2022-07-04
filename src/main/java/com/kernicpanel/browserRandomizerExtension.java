package com.kernicpanel;

import com.bitwig.extension.callback.NoArgsCallback;
import com.bitwig.extension.controller.api.*;
import com.bitwig.extension.controller.ControllerExtension;

import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

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

    AtomicReference<Integer> numberOfChoices = new AtomicReference<>(0);
    AtomicReference<Integer> nextChoice = new AtomicReference<>(0);
    Random rand = new Random();
    popupBrowser
            .resultsColumn()
            .entryCount()
            .addValueObserver(
                    newValue -> {
                      if (newValue > 0) {
                        numberOfChoices.set(newValue);
                      }
                    });

    documentState
            .getSignalSetting("Select", "browser", "Select random item")
            .addSignalObserver(
                    selectRandomItem(
                            host, popupBrowser, cursorTrack, numberOfChoices, nextChoice, rand, false));
    documentState
            .getSignalSetting("Add", "browser", "Add current item")
            .addSignalObserver(popupBrowser::commit);
    documentState
            .getSignalSetting("Random", "browser", "Surprise me!")
            .addSignalObserver(
                    selectRandomItem(
                            host, popupBrowser, cursorTrack, numberOfChoices, nextChoice, rand, true));
  }

  private NoArgsCallback selectRandomItem(
          ControllerHost host,
          PopupBrowser popupBrowser,
          CursorTrack cursorTrack,
          AtomicReference<Integer> numberOfChoices,
          AtomicReference<Integer> nextChoice,
          Random rand,
          Boolean commit) {
    return () -> {
      if (!popupBrowser.exists().getAsBoolean()) {
        cursorTrack.endOfDeviceChainInsertionPoint().browse();
      }
      // go back to first item
      // unfortunately, selecting the first item does not work
      // popupBrowser.selectFirstFile();
      // TODO: replace this ugly trick with a proper way to select the first item
      IntStream.range(0, numberOfChoices.get())
              .forEach(
                      i -> {
                        host.println("up " + i);
                        popupBrowser.selectPreviousFile();
                      });

      // go to next random choice
      // once again, I did not find a proper way to select a specific item
      if (numberOfChoices.get() > 0) {
        nextChoice.set(rand.nextInt(numberOfChoices.get()));
        host.println("nextChoice " + nextChoice.get());
        IntStream.range(0, nextChoice.get())
                .forEach(
                        i -> {
                          host.println("down " + i);
                          popupBrowser.selectNextFile();
                        });

        if (commit) {
          host.scheduleTask(popupBrowser::commit, 300);
        }
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
