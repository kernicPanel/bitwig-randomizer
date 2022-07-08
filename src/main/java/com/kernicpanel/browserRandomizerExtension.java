package com.kernicpanel;

import com.bitwig.extension.callback.NoArgsCallback;
import com.bitwig.extension.controller.api.*;
import com.bitwig.extension.controller.ControllerExtension;
import net.datafaker.Faker;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;

public class browserRandomizerExtension extends ControllerExtension {
  protected browserRandomizerExtension(
      final browserRandomizerExtensionDefinition definition, final ControllerHost host) {
    super(definition, host);
  }

  private void printer(ControllerHost host, String s) {
    host.println(s);
    java.lang.System.out.println(s);
  }

  Random rand = new Random();
  Faker faker = new Faker();

  @Override
  public void init() {
    final ControllerHost host = getHost();

    final DocumentState documentState = host.getDocumentState();
    final PopupBrowser popupBrowser = host.createPopupBrowser();
    popupBrowser.exists().markInterested();
    popupBrowser.resultsColumn().entryCount().markInterested();
    final CursorTrack cursorTrack = host.createCursorTrack(0, 0);

    BrowserResultsItemBank resultsItemBank = popupBrowser.resultsColumn().createItemBank(100000);

    SettableBooleanValue useDate =
        host.getPreferences().getBooleanSetting("Prepend date for filename", "Random name", true);

    documentState
        .getSignalSetting("Select", "Randomize browser selection", "Select random item")
        .addSignalObserver(
            selectRandomItem(host, popupBrowser, cursorTrack, resultsItemBank, rand));
    documentState
        .getSignalSetting("Add", "Randomize browser selection", "Add current item")
        .addSignalObserver(popupBrowser::commit);

    SettableStringValue filenameOutput =
        documentState.getStringSetting("Filename", "Random name", 50, "");
    SettableStringValue nameOutput = documentState.getStringSetting("Name", "Random name", 50, "");
    documentState
        .getSignalSetting(" ", "Random name", "Generate")
        .addSignalObserver(randomName(useDate, filenameOutput, nameOutput));
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

  private NoArgsCallback randomName(
      SettableBooleanValue useDate,
      SettableStringValue filenameOutput,
      SettableStringValue nameOutput) {
    return () -> {
      String[] moods = {faker.mood().emotion(), faker.mood().tone(), faker.mood().feeling()};
      String mood = moods[rand.nextInt(moods.length)];

      String[] names = {
        faker.superhero().power(),
        faker.hacker().ingverb(),
        faker.hacker().noun(),
        faker.hacker().verb(),
      };
      String name = names[rand.nextInt(names.length)];

      String[] generators = {mood, name};
      String generatedString = String.join(" ", Arrays.asList(generators));
      nameOutput.set(generatedString.toLowerCase(Locale.ROOT));

      if (useDate.get()) {
        LocalDate dateObj = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String date = dateObj.format(formatter);
        generatedString = date + "_" + generatedString;
      }

      filenameOutput.set(generatedString.replace(" ", "_").toLowerCase(Locale.ROOT));
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
