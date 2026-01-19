const BASE_URL = location.origin;
const API_URL = `${BASE_URL}/api`;

const CURRENT_URL_PATH_PREFIX = "/downloads"

function getShareId(location) {
    return location.pathname.replace(CURRENT_URL_PATH_PREFIX + "/", "");
}

function attachmentCompareFunction(a, b) {
    return a.fileName.localeCompare(b.fileName);
}

async function fetchAttachmentsForShare(shareId) {
    const attachmentListEndpoint = `${API_URL}/shares/${shareId}/attachments`;

    const response = await fetch(attachmentListEndpoint);
    const body = await response.json();

    if (response.ok) {
        return body.sort(attachmentCompareFunction);
    }

    console.error("Failed to fetch attachments.", body);
    return null;
}

const attachmentItemTemplateElement = document.querySelector("#attachment-template");

function createAttachmentItemNode(attachment) {
    const attachmentDownloadEndpoint = `${API_URL}/shares/${attachment.share.id}/attachments/${attachment.id}/file`;

    const item = attachmentItemTemplateElement.content.cloneNode(true);

    const fileLinkElement = item.querySelector(".file-link");

    fileLinkElement.textContent = attachment.fileName;
    fileLinkElement.href = attachmentDownloadEndpoint;

    return item;
}

const attachmentListElement = document.querySelector("#attachment-list");

function displayAttachments(attachments) {
    const items = attachments.map(createAttachmentItemNode);
    items.forEach(item => attachmentListElement.appendChild(item));
}

(async () => {
    const shareId = getShareId(location);
    console.debug(`shareId: ${shareId}`);

    const attachments = await fetchAttachmentsForShare(shareId);

    if (!attachments?.length) {
        location.href = "/";
    } else {
        displayAttachments(attachments);
    }
})()
