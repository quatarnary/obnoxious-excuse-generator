well there is nothing here but if I don't break the empty page I'll never find courage to work on that so here is the first version of README.md...
you know I'll never remove the first and this one I guess lol

# So yeah, this is the ExcuseGeneratorAPI.
It should've been a resume project -which it is- but with waaaay less polish..

## What you are getting with this project.. well here is a quick rundown:
* CRUD, pagination, sorting, indexing (v1 is mainly focused on this part)
  * Used Lombok to write few lines of less code. Especially in love with the @Builder!
* Has error handling. Could be better but has.
* Most of the error messages are centralized.
* User and Role based access to endpoints (v2 is mainly focuses on this part)
* Liquibase used to manage the schema changes.
* Updated mappers with MapStruct.
* JWT to make sens of user and their roles.
  * I just mistakenly started without "ROLE_" and Spring hated it.
  * So inside the code authority used instead of roles, like hasAnyAuthority="MOD, ADMIN"
* Role based actions.
* Has a weird relationship with Unit Tests.
  * I wrote them for v1 -then authorization messed them up.
  * Wrote them for v2 service layer -then refactoring the whole code messed them up.
    * fixed 'em. but because I made everything either Interface based or DI now everything is mocked.
  * gave up from unit testing, used Postman to confirm the current logic.
    * will bring the unit tests back -because they are cool.
      * But I needed to finish this part to start applying for jobs.

### As of v2 (shh, we are not talking about the v1 is never deployed.)
  * users need an account, to do anything
  * let's say you have an account already
    * you first need to hit the login.
    * then grab your JWT.
    * then add that token to the header of every request.
    * based on your role you can do different things!!
  * 3 user types
    * a pinch of REGULAR
    * lovely dovely MODs
    * ADMINs just like me how I override the commit rule while pushing the to GitHub, they rule everything.

#### what they do? well here is another bullet list (based on my someplace, bullet lists makes things more cool)
  * REGULAR
    * can get -approved- Excuses
    * yeah that's all they can do.
  * MOD
    * can create Excuses for starters
      * mod created Excuses need approval before they can be consumed.
    * can update the Excuses they created
      * mod updates removes the approval of excuse.
    * can delete the Excuses they created
  * ADMIN
    * can create
      * auto approved by default
    * can update even if they are not the creator
      * auto approves when admin updates
        * if this was an open api, I can see the complaints about this feature from here.
        * why? you may ask, because I want the update for admin and to be different than each other.
    * can delete again, even if they are not the creator
    * can approve excuses

### API Endpoints (What Can You Do?)
> "Here’s what you can hit, what it does, and who’s allowed to do it."

| Method  | Endpoint                     | Who Can Use It | Description |
|---------|------------------------------|---------------|-------------|
| **POST**  | `/api/v2/users/sign-up`        | ✅ Anyone     | Register a new user |
| **POST**  | `/api/v2/users/login`          | ✅ Anyone     | Get a JWT token |
| **GET**   | `/api/v2/excuses/{id}`         | REG/MOD/ADMIN  | Get an **approved** excuse |
| **POST**  | `/api/v2/excuses/`             | MOD/ADMIN      | Create an excuse (MODs need approval) |
| **PUT**   | `/api/v2/excuses/{id}`         | MOD/ADMIN      | Update an excuse (MOD updates remove approval) |
| **DELETE**| `/api/v2/excuses/{id}`         | MOD/ADMIN      | Delete an excuse |
| **POST**  | `/api/v2/excuses/admin/{id}`   | ADMIN          | Approve an excuse |
| **GET**   | `/api/v1/excuses/`             | ⚠️ Anyone     | Get an **approved** excuse / will removed |
| **GET**   | `/api/v1/excuses/{id}`         | ⚠️ Anyone     | Get an **approved** excuse / will removed |
| **POST**  | `/api/v1/excuses/`             | ❌ DENY ALL   | DEPRECATED |
| **PUT**   | `/api/v1/excuses/{id}`         | ❌ DENY ALL   | DEPRECATED |
| **DELETE**| `/api/v1/excuses/{id}`         | ❌ DENY ALL   | DEPRECATED |
