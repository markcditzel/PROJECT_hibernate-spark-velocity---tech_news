package controllers;

import db.DBArticle;
import db.DBHelper;
import models.*;
import spark.ModelAndView;
import spark.template.velocity.VelocityTemplateEngine;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.get;
import static spark.Spark.modelAndView;
import static spark.Spark.post;

public class ArticlesController {
    public ArticlesController() {
        this.setupEndPoints();
    }

    private void setupEndPoints() {

//        EDIT

        get("/articles/:id/edit", (req, res) -> {
            Integer intArticleId = Integer.parseInt(req.params(":id"));
            Article article = DBHelper.find(Article.class, intArticleId);

            Map<String, Object> model = new HashMap<>();
            List<Category> categories = DBHelper.getAll(Category.class);
            ArticleFormat[] articleFormats = ArticleFormat.values();
            List<Editor> editors = DBHelper.getAll(Editor.class);
            List<Journalist> journalists = DBHelper.getAll(Journalist.class);

            model.put("categories", categories);
            model.put("articleFormats", articleFormats);
            model.put("editors", editors);
            model.put("journalists", journalists);
            model.put("article", article);
            model.put("template", "templates/articles/edit.vtl");

            return new ModelAndView(model, "templates/layout.vtl");

        }, new VelocityTemplateEngine());


        //INDEX

        get("/articles", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            List<Article> articles = DBArticle.orderByDate();
            String matchString = req.queryParams("matchString");
            List<Article> searchResults = DBArticle.searchArticlesByTitle(matchString);
            model.put("searchResults", searchResults);
            model.put("articles", articles);
            model.put("template", "templates/articles/index.vtl");
            return new ModelAndView(model, "templates/layout.vtl");
        }, new VelocityTemplateEngine());


//        NEW

        get("/articles/new", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            List<Category> categories = DBHelper.getAll(Category.class);
            ArticleFormat[] articleFormats = ArticleFormat.values();
            List<Editor> editors = DBHelper.getAll(Editor.class);
            List<Journalist> journalists = DBHelper.getAll(Journalist.class);
            model.put("categories", categories);
            model.put("articleFormats", articleFormats);
            model.put("editors", editors);
            model.put("journalists", journalists);
            model.put("template", "templates/articles/new.vtl");
            return new ModelAndView(model, "templates/layout.vtl");
        }, new VelocityTemplateEngine());

//        //SHOW

        get("/articles/:id", (req, res) -> {
            Integer intArticleId = Integer.parseInt(req.params(":id"));
            Article article = DBHelper.find(Article.class, intArticleId);
            Map<String, Object> model = new HashMap<>();
            model.put("article", article);
            model.put("template", "templates/articles/show.vtl");
            return new ModelAndView(model, "templates/layout.vtl");
        }, new VelocityTemplateEngine());

        //POST NEW

        post("/articles", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            int journalistId = Integer.parseInt(req.queryParams("journalist"));
            int editorId = Integer.parseInt(req.queryParams("editor"));
            int categoryId = Integer.parseInt(req.queryParams("category"));
            String dateOfSubmissionString = req.queryParams("dateOfSubmission");
            Journalist journalist = DBHelper.find(Journalist.class, journalistId);
            Editor editor = DBHelper.find(Editor.class, editorId);
            Category category = DBHelper.find(Category.class, categoryId);
            ArticleFormat articleFormat = ArticleFormat.valueOf(req.queryParams("articleFormat"));
            String headline = req.queryParams("headline");
            String strapline = req.queryParams("strapline");
            String body = req.queryParams("body");

            Article article = new Article(articleFormat, headline, strapline, editor, journalist, category, body, LocalDate.parse(dateOfSubmissionString));
            DBHelper.save(article);
            res.redirect("/articles");
            return new ModelAndView(model, "templates/layout.vtl");
        }, new VelocityTemplateEngine());

        // POST UPDATE

        post("/articles/:id", (req, res) -> {

            Integer intArticleId = Integer.parseInt(req.params(":id"));
            Article article = DBHelper.find(Article.class, intArticleId);
            int journalistId = Integer.parseInt(req.queryParams("journalist"));
            int editorId = Integer.parseInt(req.queryParams("editor"));
            int categoryId = Integer.parseInt(req.queryParams("category"));
            Journalist journalist = DBHelper.find(Journalist.class, journalistId);
            Editor editor = DBHelper.find(Editor.class, editorId);
            Category category = DBHelper.find(Category.class, categoryId);
            ArticleFormat articleFormat = ArticleFormat.valueOf(req.queryParams("articleFormat"));
            String headline = req.queryParams("headline");
            String strapline = req.queryParams("strapline");
            String body = req.queryParams("body");
            String dateOfSubmissionString = req.queryParams("dateOfSubmission");
            LocalDate dateOfSubmission = LocalDate.parse(dateOfSubmissionString);

            article.setJournalist(journalist);
            article.setEditor(editor);
            article.setCategory(category);
            article.setArticleFormat(articleFormat);
            article.setHeadline(headline);
            article.setStrapline(strapline);
            article.setBody(body);
            article.setDateOfSubmission(dateOfSubmission);
            DBHelper.update(article);
            res.redirect("/articles");
            return new ModelAndView(new HashMap<String, Object>(), "templates/layout.vtl");

        }, new VelocityTemplateEngine());

        post ("/articles/:id/delete", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));
            Article articleToDelete = DBHelper.find(Article.class, id);
            DBHelper.delete(articleToDelete);
            res.redirect("/articles");
            return new ModelAndView(new HashMap<String, Object>(), "templates/layout.vtl");
        }, new VelocityTemplateEngine());
    }
}
