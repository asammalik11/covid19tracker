"# covid19tracker" 

covid19tracker by Team Riot

There are 100,00+ COVID19 cases every day and many of these cases go untracked. Every federal government is looking for a solution to trace down people who’ve encountered someone infected by COVID19 virus and alert them to get tested and/or quarantine. We believe that the use of Bluetooth technology and cloud databases can offer a solution.
For our 2020 IBM NA Hackathon, team riot has developed covid19tracker app. The main functionality of the app is to track down individuals who have come in contact with the client user and alert them in the event the client user is displaying COVID19 symptoms. The app uses Bluetooth technology to detect mobile devices that have come within ~10 meters of the client user and stores the MAC address for these devices on a remote server (CLoudant). In the event, the client user is displaying COVID19 symptoms, he/she can issue an alert to all MAC addresses they’ve encountered. All user with the covid19tracker app who were in the list of encountered devices will be notified that someone they've been in contact with is showing COVID19 symptoms.

How it works?
- User will download app and upon launch, register their MAC address to Cloudant database
- User can manually update list of nearby Bluetooth devices by pressing the “Refresh” button. After every refresh, list of encountered devices by user’s device will be added to Cloudant database
- If the user wishes to alert other people who were nearby the user at some point, he/she can press the “Alert” button
- All registered MAC addresses that were also encountered by the user will be notified that they are at risk of COVID19
- Cloudant database is stored locally and remotely.

MVP Shortcomings
- Only discoverable Bluetooth devices are added to list of encountered devices
- User must hit “Refresh” to update list of encountered devices
- User must manually enter their MAC address upon app launch
- All database nodes are available to all app users (privacy issue)
- Database user and pass are exposed (security issue)
- A single document is being used to keep track all information (expandability issue)

Next Steps
- Implement push notification to alert users instead of in-app message
- Authenticate users to prevent misuse
- Add Google-Map with pins to show location of each encountered device
- Automatically update Bluetooth list
- Keep list of encountered devices for 14 days
