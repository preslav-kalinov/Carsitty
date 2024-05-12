# Carsitty
An open source auto parts management software.

## Used Technologies
- Java 11;
- MySQL;
- Spring REST;
- Chart.js
- RestFB;
- JavaScript;
- jQuery;
- HTML5;
- CSS3;
- Material Design Bootstrap UI kit;

## How to run
1. Clone this repository.
2. Create a new database using the `carsitty.sql` file from the `sql` folder.
3. Create a new MySQL user that has `SELECT`, `INSERT`, `UPDATE` and `DELETE` permissions on the newly created database.
4. Go to the `frontend/js` directory and edit the `config.js` file to point to the Carsitty REST API.
5. Rename the `application.properties.dist` file in the `src/backend/config` folder to `application.properties` and modify it as follows:
- `COMPANY_NAME` - your company's name
- `PART_LIMIT` - responsible for sending emails to the users with role `Manager` if the current part is below the configured limit
- `LOG_FILES_PATH` - the path for the log file, the following pattern should be followed `/LOG_FILE_PAH/LOG_FILE_NAME.log`
- `DB_BACKUP_PATH` - the path where the database backup will be created
- `MYSQL_BIN_PATH` - the path for the mysql executable (the `bin` directory have to be provided)
- `MASTER_ADMIN_PASSWORD` - the master admin password that the Carsitty application will be using to connect to the Carsitty database (it is on a one-time-set principle)
- `PAGE_TOKEN` - Meta page token that will be used to post to Facebook. Important information:
    - You need to have a Facebook page that is managed by you in order to generate a token
    - The token is generated from Meta's [Graph API Explorer](https://developers.facebook.com/tools/explorer/). You can learn more on how to acquire it [here](https://docs.squiz.net/funnelback/docs/latest/build/data-sources/facebook/facebook-page-access-token.html)
    - Required token permissions: `pages_show_list`, `business_management`, `pages_read_management`, `pages_manage_posts`, `public_profile`
    - You have to submit your Meta app for review, in order your posts to be publicly visible on your Facebook page that have been published via Carsitty, otherwise only you will see them
        - The app review for Carsitty, being open source, should be straightforward. However, it might necessitate some screenshots or videos. You can set up a test instance to gather these
- `PAGE_ID` - your Facebook page ID
- `SMTP_HOST`- the SMTP host that Carsitty will be connecting to in order to send mail
- `SMTP_USERNAME` - the SMTP username (email) that Carsitty will be sending emails from
- `SMTP_PASSWORD`- the password, associated with the `SMTP_USERNAME` account
    - Due to security constraints, the Carsitty backend is unable to provide such detailed errors. Consequently, attempting direct connection to Gmail using your account password may result in a `500 Internal Server Error`
    - You will have to setup an App Password, as described by Google [here](https://support.google.com/accounts/answer/185833)
- `SMTP_PORT` - the port of the `SMTP_HOST` that accepts SMTP connections
- `SMTP_ENABLE_STARTTLS`- true/false - this setting directs the SMTP client to inform the mail server whether email contents should be encrypted. It's recommended to set it to `true`. For further details check [here](https://www.anubisnetworks.com/blog/ssl_and_tls_explained_in_5_minutes)
5. Run the app using `mvn spring-boot:run`.

## Notes
- Make sure that the host of the web server that serves the files is added in the allowed origins in the REST API configuration.
- Presently, there isn't an available update channel. However, you can access the most recent sources by downloading them from the [GitHub repository](https://github.com/preslav-kalinov/Carsitty). If you've already cloned it, simply execute `git pull -r` to update.
- Design created with [Material Design Bootstrap](https://mdbootstrap.com)

*P. Kalinov, 2024*

