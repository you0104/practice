"use strict";

// 要素を取得する
const input = document.querySelector("#todo_input");
const addBtn = document.querySelector("#add_btn");
const errMsg = document.querySelector("#err_msg");
const todoList = document.querySelector("#todo_list");

// ［追加］ボタンのクリックイベントに対応する
addBtn.addEventListener("click", insertBtnClicked);

// 入力欄で Enter キーでも追加できるようにする
input.addEventListener("keydown", (event) => {
	if (event.key === "Enter") {
		insertBtnClicked();
	}
});

// タスクを追加する
function insertBtnClicked() {
	const text = input.value.trim();

	// タスク内容が空の場合はエラーにする
	if (text === "") {
		errMsg.textContent = "タスクを入力してください。";
		return;
	}

	// エラーメッセージをクリアする
	errMsg.textContent = "";

	// li 要素を作成する
	const li = document.createElement("li");
	li.textContent = text;

	// 完了ボタン要素を作成する
	const completeBtn = document.createElement("button");
	completeBtn.textContent = "完了";

	// 完了ボタンのクリックイベントに対応する
	completeBtn.addEventListener("click", completeBtnClicked);

	// li 要素にボタン要素を追加する
	li.appendChild(completeBtn);

	// 画面上の要素（リスト）に追加する
	todoList.appendChild(li);

	// 入力欄を空にして次の入力に備える
	input.value = "";
	input.focus();
}

// タスクを完了する
function completeBtnClicked(event) {
	// クリックされたボタンの親要素（li）のクラスを付け外しする
	const li = event.target.parentNode;
	li.classList.toggle("complete");
}
