# 1. 
**Задача:** вывести количество пользователей, которые не создали ни одного поста. \
**Запрос:**
```
SELECT COUNT(*) FROM post
RIGHT OUTER JOIN profile ON post.profile_id = profile.profile_id
WHERE post.post_id IS NULL
```
**Ответ:** 5.
# 2.
**Задача:** выбрать по возрастанию ID всех постов, у которых 2 комментария, `title` начинается с цифры, а длина `content` больше `20` (все три условия должны соблюдаться одновременно). \
**Запрос:**
```
SELECT post.post_id FROM post
INNER JOIN comment ON comment.post_id = post.post_id
WHERE post.title ~ '^[0-9]' AND LENGTH(post.content) > 20
GROUP BY post.post_id
HAVING COUNT(comment.comment_id) = 2
```
**Ответ:** \
22 \
24 \
26 \
28 \
32 \
34 \
36 \
38 \
42 \
44 
# 3.
**Задача:** выбрать по возрастанию ID всех первых 10 постов, у которых либо нет комментариев, либо он один. \
**Запрос:**
```
SELECT post.post_id FROM post
LEFT OUTER JOIN comment ON comment.post_id = post.post_id
GROUP BY post.post_id
HAVING COUNT(comment.comment_id) <= 1
LIMIT 10
```
**Ответ:** \
1 \
3 \
5 \
7 \
9 \
11 \
13 \
15 \
17 \
19 
