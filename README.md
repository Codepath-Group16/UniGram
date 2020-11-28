# Unit 8: Group Milestone

## UniGram

### Table of Contents

1. [Overview](#Overview)
2. [Progress](#Progress)
3. [Product Spec](#Product-Spec)
4. [WireFrames](#WireFrames)

### Overview

#### Description

An Instagram-like social media app that allows universities/colleges and their enrolled students to host/post pictures and information relating to their campuses for
interested and prospective applicants.

#### App Evaluation

- **Category:** Social Networking / Education / University / Colleges
- **Mobile:** This would be primarily a Mobile app; however, it may be just as viable on a computer, such as tinder or other similar apps. The apps functionality is not only limited to mobile devices; however, the mobile version could potentially have more features.
- **Story:** Analyzes users information, such as location, intended majors/minors, financials, personal interests such as scholarships or financial aids, etc., and connects them to matching colleges and universities. Colleges and universities can control information and media contents posted on their page, and enrolled students can share their experiences on the school's page with permissions from the administrators.
- **Market:** Any individual could choose to use this app.
- **Habit:** The usage of this app can be as often or as unoften as the user wants, depending on:
  - how much they want to learn about their interested/intended colleges/universities,
  - what exactly they're looking for, and
  - if they find the information posted on the school's page helpful. Colleges/Universities can also edit/change information and media contents on their page as they desire and use the app for advertising and introducing their schools as much as they want or find it helpful.
- **Scope:** First, we would start with matching users to their universities/colleges. Then this could evolve into an experience sharing application as well to broaden its usage in which students can directly connect and socialize with some enrolled students at their matching universities/colleges.

### Progress

#### Completed user-stories

- [x] User signup and login
- [x] Gallery view of user's saved images
- [x] Bottom navigation

#### Current Progress GIFs

![Authentication](https://github.com/Codepath-Group16/UniGram/blob/main/vediowalkthrough.gif)

![gallery_night](https://user-images.githubusercontent.com/22187034/100414114-41ea9200-3079-11eb-9b5f-303793529b82.gif)

### Product Spec

#### 1. User Stories (Required and Optional)

**Must-have Stories** (Required)

- User logs in to access previous pictures and profile
- User can choose their favourite school. Use can select the school to see
- User can follow and unfollow a school depending on their preference
- Specific users(current students and admin) can take a photo
- Profile pages for each user
- Settings (Accessibility, Notification, General, etc.)

**Nice-to-have Stories** (Optional)

- User can search a database of schools to view their lifestyle
- Most searched schools recommendation
- Direct messages to students who posted the pictures about their school

#### 2. Screen Archetypes

- Login
- Register - User signs up or logs in to their account
  - Users will have different viewing privileges and will have necessary access depending on how they register.
- Home Screen - this will contain pictures of the schools the user is following, or the on they have searched
- Photo capture screen(only available to some users with specific privileges)
- Profile Screen
  - Allows a user to upload a photo and fill in information that is interesting to them and others
- Settings Screen
  - Lets people change language, and app notification settings.

#### 3. Navigation

**Tab Navigation** (Tab to Screen)

- Home
- Photo (_available to particular users with specific privileges_)
- Search
- Profile

Optional:

- Chat screen

**Flow Navigation** (Screen to Screen)

- Forced Log-in -> Account creation if no login is available
- Random college pictures if the user follows no school
- Profile -> Text field to be modified.
- Settings -> Toggle settings

### WireFrames

![WireFrame image](./wire_frame.jpg)

#### [BONUS] Digital WireFrames & Mockups

![Digital WireFrame image](./digital_wire_frame.png)

#### [BONUS] Interactive Prototype

[Figma Prototype](https://www.figma.com/proto/6mE6wzLmnza2c3u9735k1M/UniInsta?node-id=14%3A1&scaling=min-zoom)
## Schema 
### Models
#### Post

   | Property      | Type     | Description |
   | ------------- | -------- | ------------|
   | objectId      | String   | unique id for the user's post |
   | author        | Pointer to User| post/image author |
   | image         | File     | image that user posts |
   | caption       | String   | post caption by author |
   | commentsCount | Number   | number of comments that has been posted to post |
   | likesCount    | Number   | number of likes for the post |
   | createdAt     | DateTime | date when post is created (default field) |
   | updatedAt     | DateTime | date when post is last updated (default field) |
   | deletedAt     | DateTime | date when post is deleted (default field) |
   
   #### Comments
   | Property      | Type     | Description |
   | ------------- | -------- | ------------|
   | comment       | String   | post comment by viewer |
   | commentId     | String   | unique id for the each comment |
   | commenter     | Pointer to Viewer| comment author |
   | commentLikesCount    | Number   | number of likes for the comment |
   | repliesCount | Number   | number of replies that has been posted to a comment |
   | createdAt     | DateTime | time when comment is created (default field) |
   | updatedAt     | DateTime | time when comment is last updated (default field) |
   | deletedAt     | DateTime | time when comment is deleted (default field) |

   #### Users
   | Property      | Type     | Description |
   | ------------- | -------- | ------------|
   | adminId      | String   | unique id for the school |
   | userId       | String   | unique id for a user |


   
   


### Networking
