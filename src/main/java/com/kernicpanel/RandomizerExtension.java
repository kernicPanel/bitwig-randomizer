package com.kernicpanel;

import com.bitwig.extension.callback.NoArgsCallback;
import com.bitwig.extension.controller.api.*;
import com.bitwig.extension.controller.ControllerExtension;
import net.datafaker.Faker;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;
import java.util.stream.IntStream;

public class RandomizerExtension extends ControllerExtension {
  Random rand = new Random();
  Faker faker = new Faker();

  private SettableBooleanValue useDate;
  private SettableStringValue dateFormatTemplate;
  private static final String DEFAULT_DATE_FORMAT_TEMPLATE = "yyyy-MM-dd_";

  private ControllerHost host;
  private Application application;
  private DocumentState documentState;
  private PopupBrowser popupBrowser;
  private CursorTrack cursorTrack;
  private CursorDevice cursorDevice;
  private CursorRemoteControlsPage remoteControlsPage;
  private BrowserResultsItemBank resultsItemBank;
  private Preferences preferences;

  private SettableStringValue filenameOutput;
  private SettableStringValue nameOutput;
  private RemoteControl parameter;
  private SettableRangedValue randomRatio;

  protected RandomizerExtension(
      final RandomizerExtensionDefinition definition, final ControllerHost host) {
    super(definition, host);
  }

  private void printer(String s) {
    host.println(s);
    java.lang.System.out.println(s);
  }

  @Override
  public void init() {
    host = getHost();
    printer("RandomizerExtension initialized");
    application = host.createApplication();

    documentState = host.getDocumentState();
    popupBrowser = host.createPopupBrowser();
    preferences = host.getPreferences();
    popupBrowser.exists().markInterested();
    popupBrowser.resultsColumn().entryCount().markInterested();
    cursorTrack = host.createCursorTrack(0, 0);
    preferences = host.getPreferences();
    cursorDevice = cursorTrack.createCursorDevice();
    remoteControlsPage = cursorDevice.createCursorRemoteControlsPage(80);

    remoteControlsPage.pageCount().markInterested();
    remoteControlsPage.pageNames().markInterested();
    remoteControlsPage.selectedPageIndex().markInterested();

    for (int parameterIndex = 0; parameterIndex < 8; parameterIndex++) {
      remoteControlsPage.getParameter(parameterIndex).name().markInterested();
    }

    resultsItemBank = popupBrowser.resultsColumn().createItemBank(100000);

    // Naming settings
    useDate = preferences.getBooleanSetting("Prefix filename with date", "Random name", true);
    dateFormatTemplate =
        preferences.getStringSetting(
            "Format string for date prefix", "Random name", 15, DEFAULT_DATE_FORMAT_TEMPLATE);
    dateFormatTemplate.addValueObserver(
        value -> {
          try {
            LocalDate.now().format(DateTimeFormatter.ofPattern(value));
          } catch (IllegalArgumentException | UnsupportedTemporalTypeException e) {
            dateFormatTemplate.set(DEFAULT_DATE_FORMAT_TEMPLATE);
            host.showPopupNotification("Invalid date format template.");
          }
        });

    // Naming controls
    filenameOutput = documentState.getStringSetting("Filename", "Random name", 50, "");
    nameOutput = documentState.getStringSetting("Name", "Random name", 50, "");
    documentState.getSignalSetting(" ", "Random name", "Generate").addSignalObserver(randomName());

    // Browser selection controls
    documentState
        .getSignalSetting("Select", "Randomize browser selection", "Select random item")
        .addSignalObserver(selectRandomItem());
    documentState
        .getSignalSetting("Add", "Randomize browser selection", "Add current item")
        .addSignalObserver(popupBrowser::commit);

    // Parameters controls
    randomRatio =
        documentState.getNumberSetting("Ratio", "Randomize Device parameters", 0, 100, 1, "%", 15);
    documentState
        .getSignalSetting(" ", "Randomize Device parameters", "Randomize current page")
        .addSignalObserver(randomizeCurrentPageDeviceParameters());
    documentState
        .getSignalSetting("  ", "Randomize Device parameters", "Randomize all parameters")
        .addSignalObserver(randomizeDeviceParameters());
  }

  private NoArgsCallback selectRandomItem() {
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

  private NoArgsCallback randomName() {
    return () -> {
      String[] prefixes = {
          faker.mood().emotion(),
          faker.mood().tone(),
          faker.mood().feeling(),
          faker.color().name(),
          faker.coffee().intensifier(),
          faker.company().buzzword(),
          faker.dungeonsAndDragons().languages(),
          faker.size().adjective(),
          faker.subscription().subscriptionTerms(),
      };
//      printer(Arrays.toString(prefixes));
      String prefix = prefixes[rand.nextInt(prefixes.length)];

      String[] names = {
//          faker.superhero().power(),
//          faker.superhero().name(),
          faker.hacker().ingverb(),
          faker.hacker().noun(),
          faker.hacker().verb(),
          faker.food().fruit(),
          faker.food().ingredient(),
          faker.food().vegetable(),
          faker.verb().ingForm(),
          faker.verb().past(),
          faker.verb().pastParticiple(),
          faker.appliance().equipment(),
          faker.coffee().body(),
          faker.darkSoul().classes(),
          faker.darkSoul().stats(),
          faker.pokemon().type(),
          faker.dessert().flavor(),
      };
//      printer(Arrays.toString(names));
      String name = names[rand.nextInt(names.length)];

      String[] suffixes = {
          faker.animal().name(),
          faker.weather().description(),
          faker.address().streetSuffix(),
          faker.address().citySuffix(),
          faker.battlefield1().classes(),
          faker.coffee().descriptor(),
          faker.company().profession(),
          faker.construction().heavyEquipment(),
          faker.construction().materials(),
          faker.cosmere().shards(),
          faker.cosmere().surges(),
          faker.cosmere().knightsRadiant(),
          faker.cosmere().metals(),
          faker.cosmere().allomancers(),
          faker.cosmere().feruchemists(),
          faker.dessert().variety(),
          faker.dessert().topping(),
          faker.dungeonsAndDragons().klasses(),
          faker.dungeonsAndDragons().meleeWeapons(),
          faker.dungeonsAndDragons().monsters(),
          faker.dungeonsAndDragons().races(),
          faker.dungeonsAndDragons().rangedWeapons(),
          faker.electricalComponents().active(),
          faker.electricalComponents().passive(),
          faker.electricalComponents().electromechanical(),
          faker.house().furniture(),
          faker.house().room(),
          faker.pokemon().move(),
          faker.restaurant().nameSuffix(),
          faker.science().element(),
          faker.science().quark(),
          faker.science().leptons(),
          faker.science().bosons(),
          faker.science().tool(),
          faker.team().creature(),
      };
//      printer(Arrays.toString(suffixes));
      String suffix = suffixes[rand.nextInt(suffixes.length)];

      String[] generators = {prefix, name, suffix};
      String generatedString = String.join(" ", Arrays.asList(generators));
      nameOutput.set(generatedString.toLowerCase(Locale.ROOT));

      if (useDate.get()) {
        LocalDate dateObj = LocalDate.now();
        String formattedDate;
        try {
          formattedDate = dateObj.format(DateTimeFormatter.ofPattern(dateFormatTemplate.get()));
        } catch (IllegalArgumentException | UnsupportedTemporalTypeException e) {
          // Should not happen, unless e.g. the config data got corrupted.
          dateFormatTemplate.set(DEFAULT_DATE_FORMAT_TEMPLATE);
          formattedDate = dateObj.format(DateTimeFormatter.ofPattern(dateFormatTemplate.get()));
        }
        generatedString = formattedDate + generatedString;
      }

      filenameOutput.set(generatedString.replace(" ", "_").toLowerCase(Locale.ROOT));
    };
  }

  private NoArgsCallback randomizeCurrentPageDeviceParameters() {
    return () -> {
      IntStream.range(0, 8)
          .forEach(
              parameterIndex -> {
                double randomValue = rand.nextDouble();
                parameter = remoteControlsPage.getParameter(parameterIndex);
                String parameterName = parameter.name().get();

                if (!parameterName.trim().isEmpty()
                    && !parameterName.toLowerCase(Locale.ROOT).equals("output")) {
                  double newValue = ((2 * randomValue) - 1) * randomRatio.get();
                  parameter.inc(newValue);
                }
              });
    };
  }

  private NoArgsCallback randomizeDeviceParameters() {
    return () -> {
      IntStream.range(0, remoteControlsPage.pageCount().get())
          .forEachOrdered(
              (pageIndex) -> {
                randomizeCurrentPageDeviceParameters().call();
                try {
                  remoteControlsPage.selectNextPage(true);
                  Thread.sleep(10);
                } catch (InterruptedException e) {
                  e.printStackTrace();
                }
              });
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
