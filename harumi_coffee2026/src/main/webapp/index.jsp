<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>はるみコーヒー販売支援システム</title>
<link rel="stylesheet" href="css/style.css">
</head>
<body>
	<header class="app-header">
		<img src="img/logo.png" alt="はるみコーヒー ロゴ">
		<span class="title">はるみコーヒー販売支援システム</span>
	</header>
	<main>
		<div class="card">
			<h1>表示パターンの選択</h1>
			<p>
				本システムは学習用に、同じ機能のJSPを2パターン用意しています。<br>
				どちらのパターンで利用するか選択してください。<br>
				（ログイン後もメニューからいつでも切り替えられます）
			</p>
			<ul class="menu-list">
				<li>
					<a class="btn" href="login?mode=el">EL/JSTL 使用版で開始</a>
				</li>
				<li>
					<a class="btn secondary" href="login?mode=noel">スクリプトレット版（EL/JSTL 未使用）で開始</a>
				</li>
			</ul>
			<p class="pattern-note">
				EL/JSTL版：<code>\${ }</code> 記法と <code>&lt;c:forEach&gt;</code> などのJSTLタグで画面を構築。<br>
				スクリプトレット版：<code>&lt;% %&gt;</code> / <code>&lt;%= %&gt;</code> のJavaコードで画面を構築。
			</p>
		</div>
	</main>
</body>
</html>
